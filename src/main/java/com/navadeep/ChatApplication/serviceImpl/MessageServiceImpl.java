package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.MessageDao;
import com.navadeep.ChatApplication.dao.MessageReceiptDao;
import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.netty.SessionManager;
import com.navadeep.ChatApplication.netty.WsResponse;
import com.navadeep.ChatApplication.service.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageServiceImpl implements MessageService {

    private MessageDao messageDao;
    private MessageReceiptService messageReceiptService;
    private LookupService lookupService;
    private ConversationService conversationService;
    private ConversationParticipantService conversationParticipantService;
    private UserService userService;
    private AttachmentService attachmentService;
    private SessionManager sessionManager;

    public MessageServiceImpl(MessageDao messageDao,
                              MessageReceiptService messageReceiptService,
                              ConversationService conversationService,
                              ConversationParticipantService conversationParticipantService,
                              UserService userService,
                              AttachmentService attachmentService,
                              LookupService lookupService,
                              SessionManager sessionManager) {
        this.messageDao = messageDao;
        this.messageReceiptService = messageReceiptService;
        this.conversationService = conversationService;
        this.lookupService = lookupService;
        this.userService = userService;
        this.attachmentService = attachmentService;
        this.conversationParticipantService = conversationParticipantService;
        this.sessionManager = sessionManager;
    }

    @Override
    public Message sendMessage(Long senderId, Long conversationId, String messageContent, byte[] attachment,String attachmentName) {
        UserLite sender = userService.findById(senderId);
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setConversationId(conversationId);
        newMessage.setBody(messageContent);
        newMessage.setEditedAt(null);
        newMessage.setCreatedAt(LocalDateTime.now());

        if(attachment!=null && attachment.length>0 && attachmentName!=null){
            Attachment messageAttachment = attachmentService.save(attachmentName,attachment);
            newMessage.setAttachment(messageAttachment);
        }

        Message savedMessage = messageDao.save(newMessage);
        System.out.println("message saved in db");
        conversationService.updateLastMessage(conversationId,savedMessage);

        System.out.println("in sendMessge after lastmessage");


        List<Long> participants = conversationParticipantService.findParticipantUserIdsByConversationId(conversationId);
        System.out.println("participant ids : "+participants);
        // broadcast messages to all participants
        WsResponse wsResponse = WsResponse.success("newMessage",savedMessage);
        sessionManager.broadcast(wsResponse,participants);

        System.out.println("in sendMessge after broadcast");

        // in future can be processed through queues
        List<MessageReceipt> messageReceipts = new ArrayList<>();
        Lookup sentLookupStatus = lookupService.findByLookupCode("SENT");

        for (Long participantUserId : participants) {
            MessageReceipt messageReceipt = new MessageReceipt();
            messageReceipt.setMessage(savedMessage);
            messageReceipt.setUserId(participantUserId);
            messageReceipt.setStatus(sentLookupStatus);
            messageReceipts.add(messageReceipt);
        }

        messageReceiptService.saveOrUpdateMessageReceipts(messageReceipts);

        return savedMessage;

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
                    .findParticipantUserIdsByConversationId(updatedMessage.getConversationId())
                    .stream()
                    .filter(uId -> !(userId.equals(uId)))
                    .toList();


        // broadcast to participants (participants)
        WsResponse wsResponse = WsResponse.success("editedMessage",updatedMessage);
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

        myReceipt.setStatus(lookupService.findByLookupCode("DELETED"));

        messageReceiptService.update(myReceipt);

        // broadcast to userId
        WsResponse wsResponse = WsResponse
                .success("deletedMessage", myReceipt.getMessage());
        sessionManager.broadcast(wsResponse, List.of(userId));


    }

    @Override
    public void deleteMessageForEveryone(Long userId, Long messageId, Long conversationId) {


        ConversationParticipant conversationParticipant = conversationParticipantService
                .findByConversationAndUserId(conversationId, userId);
        if(conversationParticipant == null){
            throw new RuntimeException("You are not allowed to delete this message");
        }

        if(!conversationParticipant.getRole().getLookupCode().equals("ADMIN")){
            Message message = messageDao.findById(messageId);
            if(message != null && !message.getSender().getId().equals(userId)){
                throw new RuntimeException("Only Admins and Senders can delete a message for everyone");
            }
        }

        List<MessageReceipt> messageReceipts = messageReceiptService.findByMessageId(messageId);
        Lookup deleteLookupStatus = lookupService.findByLookupCode("DELETED");
        messageReceipts.forEach(messageReceipt -> {
            messageReceipt.setStatus(deleteLookupStatus);
        });

        messageReceiptService.saveOrUpdateMessageReceipts(messageReceipts); // uses batch updates (of  size 50)

        // broadcast to all participants (messageReceipt.userId) as deleted message
        List<Long> effectedUsers = messageReceipts.stream().map(MessageReceipt::getUserId).filter(uId -> !(userId.equals(uId))).toList();

        WsResponse wsResponse = WsResponse.success("deletedMessage",messageReceipts.getFirst().getMessage());
        sessionManager.broadcast(wsResponse,effectedUsers);

    }

    @Override
    public List<Message> getMessageByConversationId(Long userId, Long conversationId) {
        if(userId == null || conversationId == null){
            throw new RuntimeException("userId and conversationId cannot be null");
        }

        return messageDao.findByConversationId(userId,conversationId);
    }
}
