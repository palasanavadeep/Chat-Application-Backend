package com.navadeep.ChatApplication.netty;

import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.service.*;
import com.navadeep.ChatApplication.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ChatEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatEventHandler.class);
    private final SessionManager sessionManager;
    private final ConversationService conversationService;
    private final ConversationParticipantService conversationParticipantService;
    private final MessageService messageService;
    private final MessageReceiptService messageReceiptService;
    private final UserService userService;

    public ChatEventHandler(SessionManager sessionManager, ConversationService conversationService, ConversationParticipantService conversationParticipantService, MessageService messageService, MessageReceiptService messageReceiptService, UserService userService) {
        this.sessionManager = sessionManager;
        this.conversationService = conversationService;
        this.conversationParticipantService = conversationParticipantService;
        this.messageService = messageService;
        this.messageReceiptService = messageReceiptService;
        this.userService = userService;
    }

    public void sendMessageHandler(Long userId, MessageFrame msg) {
        try{
            byte[] file = getFile(msg);
            String fileName = getFileName(msg);
            Map<String, Object> data = msg.getData();

            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
            String messageContent = data.get("messageContent") != null ? data.get("messageContent").toString() : null;

            if(conversationId != null){
                messageService.sendMessage(userId,conversationId,messageContent,file,fileName);
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't Send Message : "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void editMessageHandler(Long userId,MessageFrame msg) {
        try{
            Long messageId = Long.parseLong(msg.getData().get("messageId").toString());
            String messageContent = msg.getData().get("messageContent").toString();

            messageService.editMessage(userId,messageId,messageContent);
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't Edit Message : "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void deleteMessageForMeHandler(Long userId,MessageFrame msg) {
        try{
            Long messageId = Long.parseLong(msg.getData().get("messageId").toString());
            messageService.deleteMessageForMe(userId,messageId);
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't Delete Message : "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void deleteMessageForEveryoneHandler(Long userId,MessageFrame msg) {
        try{
            Long messageId = Long.parseLong(msg.getData().get("messageId").toString());
            Long conversationId = Long.parseLong(msg.getData().get("conversationId").toString());
            messageService.deleteMessageForEveryone(userId,messageId,conversationId);
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't Delete Message : "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void createConversationHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();

            String type = data.get("type").toString();
            String name = (data.get("name") != null) ? data.get("name").toString() : null;
            String description = (data.get("description") != null) ? data.get("description").toString() : null;

            List<Long> participants = new ArrayList<>();
            if (data.get("participants") instanceof List<?> list) {
                list.forEach(p -> participants.add(Long.parseLong(p.toString())));
            }

            byte[] conversationImageFile = getFile(msg);
            String fileName = getFileName(msg);

            conversationService
                    .createConversation(userId,type,name,description,participants,conversationImageFile,fileName);

        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't Create Conversation : "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void updateConversationHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
            String name = (data.get("name") != null) ? data.get("name").toString() : null;
            String description = (data.get("description") != null) ? data.get("description").toString() : null;

            byte[] conversationImageFile = getFile(msg);
            String fileName = getFileName(msg);

            conversationService.updateConversation(userId,conversationId,name,description,conversationImageFile,fileName);

        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't update conversation : "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void addUserToConversationHandler(Long userId,MessageFrame msg) {
        try{
            System.out.println("addUserToConversationHandler called");
            Map<String, Object> data = msg.getData();
            Long newUserId = data.get("newUserId") != null ? Long.parseLong(data.get("newUserId").toString()) : null;
            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

            conversationService.addParticipant(userId,newUserId,conversationId);

        }
        catch (Exception e){
            System.out.println("addUserToConversationHandler error : "+e.getMessage());
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't add user to conversation"+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void removeUserFromConversationHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long participantId = data.get("participantId") != null ? Long.parseLong(data.get("participantId").toString()) : null;

            conversationService.removeParticipant(userId,participantId);

        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't remove user from conversation"+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void updateParticipantRoleHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long participantId = data.get("participantId") != null ? Long.parseLong(data.get("participantId").toString()) : null;
            String role = (data.get("role") != null) ? data.get("role").toString() : null;

            conversationParticipantService.updateParticipantRole(userId,participantId,role);
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't update role of participant"+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void getConversationParticipantsHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();

            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
            List<ConversationParticipant> participants = conversationService.getAllParticipants(conversationId);

            WsResponse wsResponse = WsResponse
                    .success("getConversationParticipantsResponse",
                            Map.of("participants", participants,
                                    "conversationId", conversationId));

            sessionManager.broadcast(wsResponse,List.of(userId));
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't get participants "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void getUserConversationsHandler(Long userId,MessageFrame msg) {
        try{
            List<Conversation> conversations = conversationService.getUserConversations(userId);
            WsResponse wsResponse = WsResponse.success("getUserConversationsResponse", conversations);
            sessionManager.broadcast(wsResponse,List.of(userId));
        }
        catch (Exception e){
            log.error("eroor {}",e.getMessage(),e);
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't get chats "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void getConversationHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();

            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

            Conversation conversation = conversationService.findById(conversationId);

            WsResponse wsResponse = WsResponse.success("getConversationResponse", conversation);
            sessionManager.broadcast(wsResponse,List.of(userId));
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't get Conversation "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void getProfileHandler(Long userId,MessageFrame msg) {
        try{
            User user = userService.getUserProfileById(userId);
            WsResponse wsResponse = WsResponse.success("getProfileResponse", user);
            sessionManager.broadcast(wsResponse,List.of(userId));
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't get Profile "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void getAllMessagesHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

            if(conversationId != null) {
                List<Message> conversationMessages = messageService
                        .getMessageByConversationId(userId,conversationId);

                WsResponse wsResponse = WsResponse.success("getAllMessagesResponse",
                        Map.of("conversationId" , conversationId,"messages", conversationMessages));
                sessionManager.broadcast(wsResponse,List.of(userId));
            }
        }
        catch (Exception e){
           log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't get messages of chat "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void markMessageAsReadHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long messageId =  data.get("messageId") != null ? Long.parseLong(data.get("messageId").toString()) : null;

            messageReceiptService.markMessageAsRead(userId,messageId);
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't update message as read "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void markConversationMessagesAsReadHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

            messageReceiptService.markMessagesInConversationAsRead(userId,conversationId);
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't update message/s as read "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void updateProfileHandler(Long userId, MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            String newUsername = data.get("username") != null ? data.get("username").toString() : null;
            String newDisplayName = data.get("displayName") != null ? data.get("displayName").toString() : null;
            String newEmail = data.get("email") != null ? data.get("email").toString() : null;

            byte[] profileImageFile = getFile(msg);
            String fileName = getFileName(msg);

            User updatedUser = userService.update(userId,newUsername,newDisplayName,newEmail,profileImageFile,fileName);

            WsResponse wsResponse = WsResponse.success("updateProfileResponse", updatedUser);
            sessionManager.broadcast(wsResponse,List.of(userId));

        }catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't update profile "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void leaveConversationHandler(Long userId, MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

            if(conversationId == null) {
                throw new IllegalArgumentException("Conversation ID is required to leave a conversation.");
            }

            conversationService.leaveConversation(userId,conversationId);

        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't leave conversation "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    public void searchUserHandler(Long userId,MessageFrame msg) {
        try{
            Map<String, Object> data = msg.getData();
            String username = data.get("username") != null ? data.get("username").toString() : null;
            UserLite userResult= null;
            if(username != null){
                userResult = userService.findByUsername(username);
                if(userResult == null){
                    throw new RuntimeException("User with username : "+username+" not found");
                }
                WsResponse wsResponse = WsResponse.success("searchUserResponse", List.of(userResult));
                sessionManager.broadcast(wsResponse,List.of(userId));
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
            sessionManager.broadcast(
                    WsResponse.error(Constants.STATUS_ERROR,"Can't get user/s "+e.getMessage()),
                    List.of(userId)
            );
        }
    }

    private byte[] getFile(MessageFrame msg) {
        if (msg.getFile() == null || msg.getFile().isBlank()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(msg.getFile());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String getFileName(MessageFrame msg) {
        return (msg.getFileName() != null && !msg.getFileName().isBlank())
                ? msg.getFileName()
                : null;
    }



}
