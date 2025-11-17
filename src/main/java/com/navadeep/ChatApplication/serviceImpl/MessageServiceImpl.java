package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.MessageDao;
import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.exception.BadRequestException;
import com.navadeep.ChatApplication.exception.ForbiddenException;
import com.navadeep.ChatApplication.exception.InternalServerException;
import com.navadeep.ChatApplication.exception.NotFoundException;
import com.navadeep.ChatApplication.netty.SessionManager;
import com.navadeep.ChatApplication.netty.WsResponse;
import com.navadeep.ChatApplication.service.*;
import com.navadeep.ChatApplication.utils.Constants;
import com.navadeep.ChatApplication.utils.MESSAGE_STATUS;
import com.navadeep.ChatApplication.utils.WS_ACTION;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.List;

public class MessageServiceImpl implements MessageService {

    private final SessionFactory sessionFactory;

    private final MessageDao messageDao;
    private final MessageReceiptService messageReceiptService;
    private final LookupService lookupService;
    private final ConversationService conversationService;
    private final ConversationParticipantService conversationParticipantService;
    private final UserService userService;
    private final AttachmentService attachmentService;
    private final SessionManager sessionManager;

    private static final Log log =  LogFactory.getLog(MessageServiceImpl.class);

    public MessageServiceImpl(MessageDao messageDao, MessageReceiptService messageReceiptService, ConversationService conversationService, ConversationParticipantService conversationParticipantService, UserService userService, AttachmentService attachmentService, LookupService lookupService, SessionManager sessionManager, SessionFactory sessionFactory) {
        this.messageDao = messageDao;
        this.messageReceiptService = messageReceiptService;
        this.conversationService = conversationService;
        this.lookupService = lookupService;
        this.userService = userService;
        this.attachmentService = attachmentService;
        this.conversationParticipantService = conversationParticipantService;
        this.sessionManager = sessionManager;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Message sendMessage(Long senderId, Long conversationId, String messageContent, byte[] attachment,String attachmentName) {

        Transaction tx = null;
        try(Session session=sessionFactory.openSession()){
            tx = session.beginTransaction();

            UserLite sender = userService.findById(senderId);
            Message newMessage = new Message();
            newMessage.setSender(sender);
            newMessage.setConversationId(conversationId);
            newMessage.setBody(messageContent);
            newMessage.setEditedAt(null);
            newMessage.setCreatedAt(System.currentTimeMillis());

            if(attachment!=null && attachment.length>0 && attachmentName!=null){
                Attachment messageAttachment = attachmentService.save(attachmentName,attachment);
                newMessage.setAttachment(messageAttachment);
            }

            Message savedMessage = messageDao.save(newMessage);

            conversationService.updateLastMessage(conversationId,savedMessage);


            List<Long> participants = conversationParticipantService.findParticipantUserIdsByConversationId(conversationId);
            // broadcast messages to all participants
            WsResponse wsResponse = WsResponse.success(WS_ACTION.NEW_MESSAGE,savedMessage);
            sessionManager.broadcast(wsResponse,participants);

            // in future can be processed through queues
            List<MessageReceipt> messageReceipts = new ArrayList<>();
            Lookup sentLookupStatus = lookupService.findByLookupCode(MESSAGE_STATUS.SENT);

            for (Long participantUserId : participants) {
                MessageReceipt messageReceipt = new MessageReceipt();
                messageReceipt.setMessage(savedMessage);
                messageReceipt.setUserId(participantUserId);
                messageReceipt.setStatus(sentLookupStatus);
                messageReceipts.add(messageReceipt);
            }

            messageReceiptService.saveOrUpdateMessageReceipts(messageReceipts);

            tx.commit();

            return savedMessage;
        }catch (Exception e){
            if(tx!=null){
                tx.rollback();
            }
            log.error(e.getMessage(),e);
            throw new InternalServerException("Failed to send message",e);
        }
    }

    @Override
    public Message editMessage(Long userId,Long messageId, String newMessageContent) {
        if (messageId == null) {
            log.error("messageId is null");
            throw new BadRequestException("messageId cannot be null");
        }
        Message message = messageDao.findById(messageId);
        if (message == null) {
            log.error("Message with id ["+messageId+"] not found");
            throw new NotFoundException("Message with ID : "+messageId+" not found");
        }

        if(!message.getSender().getId().equals(userId)){
            log.error("user : ["+userId+"] don't have permission to edit this message");
            throw new ForbiddenException("Only Sender can edit this Message"+messageId);
        }

        message.setBody(newMessageContent);

        Message updatedMessage = messageDao.update(message);

        List<Long> participants = conversationParticipantService
                    .findParticipantUserIdsByConversationId(updatedMessage.getConversationId());


        // broadcast to participants (participants)
        WsResponse wsResponse = WsResponse.success(WS_ACTION.EDITED_MESSAGE,updatedMessage);
//        participants.add(userId);
        sessionManager.broadcast(wsResponse,participants);

        log.info("Message with id ["+messageId+"] successfully edited");
        return updatedMessage;
    }

    @Override
    public void deleteMessageForMe(Long userId, Long messageId) {
        if (messageId == null) {
            log.error("messageId is null");
            throw new BadRequestException("messageId cannot be null");
        }
        MessageReceipt myReceipt = messageReceiptService
                .findByUserIdAndMessageId(userId, messageId);

        if(myReceipt == null){
            log.error("MessageReceipt with messageID : "+messageId+" and userId: "+userId+" not found");
            throw new NotFoundException("MessageReceipt with messageID : "+messageId+" and userId: "+userId+" not found");
        }

        myReceipt.setStatus(lookupService.findByLookupCode(MESSAGE_STATUS.DELETED));

        messageReceiptService.update(myReceipt);

        // broadcast to userId
        WsResponse wsResponse = WsResponse
                .success(WS_ACTION.DELETED_MESSAGE, myReceipt.getMessage());
        sessionManager.broadcast(wsResponse, List.of(userId));

        log.info("Message with id ["+messageId+"] successfully deleted for User : "+userId);
    }

    @Override
    public void deleteMessageForEveryone(Long userId, Long messageId, Long conversationId) {
        if (messageId == null || conversationId == null) {
            log.error("messageId or conversationId is null");
            throw new BadRequestException("messageId and conversationId cannot be null");
        }
        ConversationParticipant conversationParticipant = conversationParticipantService
                .findByConversationAndUserId(conversationId, userId);
        if(conversationParticipant == null){
            log.error("User : ["+userId+"] is not a participant of conversation "+conversationId);
            throw new ForbiddenException("You are not allowed to delete this message");
        }

        if(!conversationParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            Message message = messageDao.findById(messageId);
            if(message != null && !message.getSender().getId().equals(userId)){
                log.error("user : ["+userId+"] don't have permission to delete this message : "+messageId);
                throw new ForbiddenException("Only Admins and Senders can delete a message for everyone");
            }
        }

        List<MessageReceipt> messageReceipts = messageReceiptService.findByMessageId(messageId);
        Lookup deleteLookupStatus = lookupService.findByLookupCode(MESSAGE_STATUS.DELETED);
        messageReceipts.forEach(messageReceipt -> {
            messageReceipt.setStatus(deleteLookupStatus);
        });

        messageReceiptService.saveOrUpdateMessageReceipts(messageReceipts); // uses batch updates (of  size 50)

        // broadcast to all participants (messageReceipt.userId) as deleted message
        List<Long> effectedUsers = messageReceipts.stream().map(MessageReceipt::getUserId).toList();

        WsResponse wsResponse = WsResponse.success(WS_ACTION.DELETED_MESSAGE,messageReceipts.getFirst().getMessage());
        sessionManager.broadcast(wsResponse,effectedUsers);

        log.info("Message with id ["+messageId+"] successfully deleted for Everyone");

    }

    @Override
    public List<Message> getMessageByConversationId(Long userId, Long conversationId) {
        if(conversationId == null){
            log.error("conversationId is null");
            throw new BadRequestException("conversationId cannot be null");
        }

        ConversationParticipant participant = conversationParticipantService
                .findByConversationAndUserId(conversationId, userId);

        if(participant == null){
            log.error("User : ["+userId+"] is not a participant of conversation "+conversationId);
            throw new ForbiddenException("You are not allowed to access messages of this conversation");
        }

        return messageDao.findByConversationId(userId,conversationId);
    }
}
