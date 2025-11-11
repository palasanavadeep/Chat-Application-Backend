package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.MessageDao;
import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.netty.SessionManager;
import com.navadeep.ChatApplication.netty.WsResponse;
import com.navadeep.ChatApplication.service.*;
import com.navadeep.ChatApplication.utils.Constants;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private static final Logger log =  LoggerFactory.getLogger(MessageServiceImpl.class);


    public MessageServiceImpl(MessageDao messageDao,
                              MessageReceiptService messageReceiptService,
                              ConversationService conversationService,
                              ConversationParticipantService conversationParticipantService,
                              UserService userService,
                              AttachmentService attachmentService,
                              LookupService lookupService,
                              SessionManager sessionManager,
                              SessionFactory sessionFactory) {
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
            WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_NEW_MESSAGE,savedMessage);
            sessionManager.broadcast(wsResponse,participants);

            // in future can be processed through queues
            List<MessageReceipt> messageReceipts = new ArrayList<>();
            Lookup sentLookupStatus = lookupService.findByLookupCode(Constants.MESSAGE_STATUS_SENT);

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
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Message editMessage(Long userId,Long messageId, String newMessageContent) {
        Message message = messageDao.findById(messageId);
        if (message == null) {
            throw new RuntimeException("Message with ID : "+messageId+" not found");
        }

        if(!message.getSender().getId().equals(userId)){
            throw new RuntimeException("Only Sender can edit this Message"+messageId);
        }

        message.setBody(newMessageContent);

        Message updatedMessage = messageDao.update(message);

        List<Long> participants = conversationParticipantService
                    .findParticipantUserIdsByConversationId(updatedMessage.getConversationId());


        // broadcast to participants (participants)
        WsResponse wsResponse = WsResponse.success("editedMessage",updatedMessage);
//        participants.add(userId);
        sessionManager.broadcast(wsResponse,participants);

        return updatedMessage;
    }

    @Override
    public void deleteMessageForMe(Long userId, Long messageId) {

        MessageReceipt myReceipt = messageReceiptService
                .findByUserIdAndMessageId(userId, messageId);

        if(myReceipt == null){
            throw new RuntimeException("MessageReceipt with messageID : "+messageId+" and userId: "+userId+" not found");
        }

        myReceipt.setStatus(lookupService.findByLookupCode(Constants.MESSAGE_STATUS_DELETED));

        messageReceiptService.update(myReceipt);

        // broadcast to userId
        WsResponse wsResponse = WsResponse
                .success(Constants.WS_ACTION_DELETED_MESSAGE, myReceipt.getMessage());
        sessionManager.broadcast(wsResponse, List.of(userId));


    }

    @Override
    public void deleteMessageForEveryone(Long userId, Long messageId, Long conversationId) {


        ConversationParticipant conversationParticipant = conversationParticipantService
                .findByConversationAndUserId(conversationId, userId);
        if(conversationParticipant == null){
            throw new RuntimeException("You are not allowed to delete this message");
        }

        if(!conversationParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            Message message = messageDao.findById(messageId);
            if(message != null && !message.getSender().getId().equals(userId)){
                log.warn("user : {} don't have permission to delete this message",userId);
                throw new RuntimeException("Only Admins and Senders can delete a message for everyone");
            }
        }

        List<MessageReceipt> messageReceipts = messageReceiptService.findByMessageId(messageId);
        Lookup deleteLookupStatus = lookupService.findByLookupCode(Constants.MESSAGE_STATUS_DELETED);
        messageReceipts.forEach(messageReceipt -> {
            messageReceipt.setStatus(deleteLookupStatus);
        });

        messageReceiptService.saveOrUpdateMessageReceipts(messageReceipts); // uses batch updates (of  size 50)

        // broadcast to all participants (messageReceipt.userId) as deleted message
        List<Long> effectedUsers = messageReceipts.stream().map(MessageReceipt::getUserId).toList();

        WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_DELETED_MESSAGE,messageReceipts.getFirst().getMessage());
        sessionManager.broadcast(wsResponse,effectedUsers);

    }

    @Override
    public List<Message> getMessageByConversationId(Long userId, Long conversationId) {
        if(userId == null || conversationId == null){
            throw new RuntimeException("userId and conversationId cannot be null");
        }

        List<Message> messages = messageDao.findByConversationId(userId,conversationId);

        log.info("getMessageByConversationId {}",messages);

        return messages;
    }
}
