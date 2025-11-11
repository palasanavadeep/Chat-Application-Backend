package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.*;
import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.netty.SessionManager;
import com.navadeep.ChatApplication.netty.WsResponse;
import com.navadeep.ChatApplication.service.*;
import com.navadeep.ChatApplication.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class ConversationServiceImpl implements ConversationService {

    private final ConversationDao conversationDao;
    private final ConversationParticipantDao conversationParticipantDao;
    private final MessageReceiptService messageReceiptService;
    private final UserLiteDao userLiteDao;
    private final ConversationParticipantService conversationParticipantService;
    private final LookupService lookupService;
    private final AttachmentService attachmentService;
    private final SessionManager sessionManager;

    public ConversationServiceImpl(ConversationDao conversationDao, ConversationParticipantDao conversationParticipantDao, MessageReceiptService messageReceiptService, UserLiteDao userLiteDao, ConversationParticipantService conversationParticipantService, LookupService lookupService,AttachmentService attachmentService,SessionManager sessionManager) {
        this.conversationDao = conversationDao;
        this.conversationParticipantDao = conversationParticipantDao;
        this.messageReceiptService = messageReceiptService;
        this.userLiteDao = userLiteDao;
        this.conversationParticipantService = conversationParticipantService;
        this.lookupService = lookupService;
        this.attachmentService = attachmentService;
        this.sessionManager = sessionManager;
    }

    @Override
    public Conversation createConversation(Long userId, String type, String name, String description,
                                           List<Long> participants,byte[] conversationImageFile,String fileName) {

        if(type == null || type.isEmpty()){
            throw new IllegalArgumentException("type is null or empty");
        }

        UserLite creator = userLiteDao.findById(userId);

        Conversation newConversation = new Conversation();
        newConversation.setCreatedBy(creator);
        newConversation.setCreatedAt(System.currentTimeMillis());
        newConversation.setType(lookupService.findByLookupCode(type));


        // create participant for creator of the conversation
        ConversationParticipant creatorParticipant = generateConversationParticipant(userId, Constants.ROLE_ADMIN);

        // add members into participants list
        List<ConversationParticipant> conversationParticipants = new ArrayList<>();
        conversationParticipants.add(creatorParticipant);

        for(Long participantId : participants){
            conversationParticipants
                    .add(generateConversationParticipant(participantId,Constants.ROLE_MEMBER));
        }

        if(type.equalsIgnoreCase(Constants.CONVERSATION_TYPE_GROUP)){
            if(name == null || name.isEmpty()){
                throw new IllegalArgumentException("name is null or empty");
            }
            newConversation.setName(name);
            if(!description.isEmpty()){
                newConversation.setDescription(description);
            }
            if(conversationImageFile != null && fileName != null){
                Attachment conversationAttachment = attachmentService.save(fileName,conversationImageFile);
                newConversation.setConversationImage(conversationAttachment);
            }

        }

        newConversation.setConversationParticipants(conversationParticipants);

        // save the new conversation
        Conversation createdConversation = conversationDao.save(newConversation);

        // broadcast this new conversation message to all participants (socket)
        WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_NEW_CONVERSATION,createdConversation);
        participants.add(userId);
        sessionManager.broadcast(wsResponse,participants);


        return createdConversation;
    }

    private ConversationParticipant generateConversationParticipant(Long userId,String role){
        UserLite user =  userLiteDao.findById(userId);
        ConversationParticipant conversationParticipant = new ConversationParticipant();
        conversationParticipant.setUser(user);
        conversationParticipant.setIsMuted(false);
        conversationParticipant.setIsPinned(false);
        conversationParticipant.setCreatedAt(System.currentTimeMillis());
        conversationParticipant.setRole(lookupService.findByLookupCode(role.toUpperCase()));
        return conversationParticipant;
    }


    @Override
    public Conversation updateConversation(Long userId,Long conversationId,String name,String description,byte[] conversationImageFile,String fileName) {

        if(conversationId == null){
            throw new IllegalArgumentException("conversationId is null or empty");
        }

        ConversationParticipant checkIfParticipant = conversationParticipantService.findByConversationAndUserId(conversationId,userId);
        if(checkIfParticipant == null){
            throw new IllegalArgumentException("You are not allowed to update this conversation");
        }

        if(!checkIfParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            throw new RuntimeException("UserId: "+userId+" not allowed to update this conversation");
        }

        Conversation conversationToUpdate = conversationDao.findById(conversationId);
        if(name != null){
            conversationToUpdate.setName(name);
        }
        if(description != null){
            conversationToUpdate.setDescription(description);
        }
//
        if(conversationImageFile != null && fileName != null){
            Attachment previousAttachment = conversationToUpdate.getConversationImage();
            Attachment newAttachment = attachmentService.save(fileName,conversationImageFile);
            conversationToUpdate.setConversationImage(newAttachment);
            if(previousAttachment != null){
                attachmentService.delete(previousAttachment.getId());
            }

        }

        Conversation updatedConversation =  conversationDao.update(conversationToUpdate);

        // broadcasting to all except requestor
        List<Long> participantUserIds = updatedConversation
                .getConversationParticipants()
                .stream()
                .filter(participant -> participant.getLeftAt() == null)
                .map(participant -> participant.getUser().getId())
                .toList();

        WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_UPDATED_CONVERSATION,updatedConversation);
        sessionManager.broadcast(wsResponse,participantUserIds);

        return updatedConversation;
    }

    @Override
    public void updateLastMessage(Long conversationId,Message message) {
        if(conversationId == null || message == null){
            throw new IllegalArgumentException("conversationId and message is null");
        }
        Conversation conversation = conversationDao.findById(conversationId);
        if(conversation == null){
            throw new IllegalArgumentException("conversation is not found");
        }
        conversation.setLastMessage(message);
        conversationDao.update(conversation);
    }

    /**
     * Get all conversations of a user.
     * If personal -> fetch participants.
     * For group -> only include user’s participant info.
     */
    @Override
    public List<Conversation> getUserConversations(Long userId) {
        // Fetch user conversations
        List<Conversation> conversations = conversationDao.findUserConversations(userId);
        if (conversations.isEmpty()) return conversations;

        // Collect last message IDs
        List<Long> lastMessageIds = conversations.stream()
                .map(Conversation::getLastMessage)
                .filter(Objects::nonNull)
                .map(Message::getId)
                .toList();

        if (lastMessageIds.isEmpty()) {
            conversations.forEach(c -> c.setHasUnreadMessages(false));
            return conversations;
        }

        // Fetch message receipts in batch
        List<MessageReceipt> receiptResults = messageReceiptService
                .findMessageReceiptsByUserIdAndMessageIds(userId, lastMessageIds);

        Map<Long, Boolean> hasUnreadMap = receiptResults.stream()
                .collect(Collectors.toMap(
                        mr -> mr.getMessage().getId(),
                        mr -> {
                            String status = mr.getStatus().getLookupCode();
                            return !status.equalsIgnoreCase(Constants.MESSAGE_STATUS_READ)
                                    && !status.equalsIgnoreCase(Constants.MESSAGE_STATUS_DELETED);
                        }
                ));

        // Map computed values and participants
        for (Conversation conv : conversations) {
            // Compute unread flag
            Message lastMsg = conv.getLastMessage();
            boolean hasUnread = lastMsg != null && hasUnreadMap.getOrDefault(lastMsg.getId(), false);
            conv.setHasUnreadMessages(hasUnread);

            // filter participants
            if(!conv.getType().getLookupCode().equals(Constants.CONVERSATION_TYPE_PERSONAL)){
                conv.getConversationParticipants().removeIf(cp -> !cp.getUser().getId().equals(userId));
            }
        }

        return conversations.stream()
                .sorted((a, b) -> {
                    Long aTime = a.getLastMessage() != null ? a.getLastMessage().getCreatedAt() : 0L;
                    Long bTime = b.getLastMessage() != null ? b.getLastMessage().getCreatedAt() : 0L;
                    return bTime.compareTo(aTime); // descending (latest first)
                })
                .collect(Collectors.toList());
    }

    @Override
    public ConversationParticipant addParticipant(Long userId,Long newUserId,Long conversationId) {

        if(newUserId==null || userId==null || conversationId==null) {
            throw new IllegalArgumentException("newUserId or conversationId is null");
        }

        ConversationParticipant conversationParticipant = conversationParticipantService
                .findByConversationAndUserId(conversationId,userId);



        if(conversationParticipant == null){
            throw new IllegalArgumentException("User : "+userId+" is not in the conversation"+conversationId);
        }


        if(!conversationParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            throw new RuntimeException("UserId: "+userId+" not allowed to add participant to this conversation");
        }

        ConversationParticipant checkIfParticipant = conversationParticipantService.findByConversationAndUserId(conversationId,newUserId);
        ConversationParticipant resultParticipant;


        if(checkIfParticipant == null){
            ConversationParticipant newConversationParticipant =
                    generateConversationParticipant(newUserId,Constants.ROLE_MEMBER);
            newConversationParticipant.setLeftAt(null);
            conversationDao.addParticipant(conversationId,newConversationParticipant);
            resultParticipant = newConversationParticipant;

        }
        else{
            // already a participant but left the conversation
            checkIfParticipant.setCreatedAt(System.currentTimeMillis());
            checkIfParticipant.setLeftAt(null);
            checkIfParticipant.setRole(lookupService.findByLookupCode(Constants.ROLE_MEMBER));
            resultParticipant =  conversationParticipantDao.update(checkIfParticipant);

        }

        // broadcast to new User
        Conversation conversation = this.findById(conversationId);
        WsResponse newParticipantResponse = WsResponse.success(Constants.WS_ACTION_NEW_CONVERSATION,conversation);
        sessionManager.broadcast(newParticipantResponse,List.of(newUserId));

        WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_NEW_PARTICIPANT,
                Map.of("conversationId",conversationId,
                        "participant",resultParticipant));


        // notify all existing participants except new user
        List<Long> participantUserIds = conversation
                .getConversationParticipants()
                .stream()
                .map(participant -> participant.getUser().getId())
                .filter(id -> !id.equals(newUserId)) // exclude new user
                .toList();

        sessionManager.broadcast(wsResponse,participantUserIds);

        return resultParticipant;

    }

    @Override
    public void removeParticipant(Long userId,Long participantId) {

        ConversationParticipant checkIfParticipant = conversationParticipantService.findById(participantId);

        if(checkIfParticipant == null){
            throw new IllegalArgumentException("Participant with id : "+participantId+" not found");
        }
        Long conversationId = checkIfParticipant.getConversationId();

        ConversationParticipant isAdminParticipant = conversationParticipantService.findByConversationAndUserId(conversationId,userId);

        if(!isAdminParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            throw new RuntimeException("UserId: "+userId+" not allowed to remove participant from this conversation");
        }

        conversationDao.removeParticipant(conversationId,participantId);

        Long removedParticipantUserId = checkIfParticipant.getUser().getId();

        // to remove the conversation from the participant's view
        WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_REMOVED_CONVERSATION,
                Map.of("conversationId",conversationId));
        sessionManager.broadcast(wsResponse,
                List.of(removedParticipantUserId));

        // to notify all the existing participants except removed user
        List<Long> participantUserIds = conversationParticipantService
                .findParticipantUserIdsByConversationId(conversationId)
                .stream()
                .filter(id -> !id.equals(removedParticipantUserId))
                .toList();
        WsResponse removedParticipantResponse = WsResponse.success(Constants.WS_ACTION_REMOVED_PARTICIPANT,
                Map.of("conversationId",conversationId,
                        "participantId",participantId));
        sessionManager.broadcast(removedParticipantResponse,participantUserIds);

    }

    @Override
    public List<ConversationParticipant> getAllParticipants(Long conversationId) {
        return conversationDao.getAllParticipants(conversationId);
    }


    @Override
    public void leaveConversation(Long userId, Long conversationId) {
        ConversationParticipant participant = conversationParticipantService.findByConversationAndUserId(conversationId,userId);
        if(participant == null){
            throw new IllegalArgumentException("User : "+userId+" is not in the conversation"+conversationId);
        }
        if(participant.getUser().getId().equals(userId)){
            participant.setLeftAt(System.currentTimeMillis());
            conversationParticipantDao.update(participant);

            // broadcast to userId
            WsResponse wsResponse = WsResponse.success(Constants.WS_ACTION_REMOVED_CONVERSATION,
                    Map.of("conversationId",conversationId));
            sessionManager.broadcast(wsResponse, List.of(userId));

            // to notify all the existing participants except removed user
            List<Long> participantUserIds = conversationParticipantService
                    .findParticipantUserIdsByConversationId(conversationId)
                    .stream()
                    .filter(id -> !id.equals(participant.getUser().getId()))
                    .toList();

            WsResponse removedParticipantResponse = WsResponse.success(Constants.WS_ACTION_REMOVED_PARTICIPANT,
                    Map.of("conversationId",conversationId,
                            "participantId",participant.getUser().getId()));
            sessionManager.broadcast(removedParticipantResponse,participantUserIds);
        }
    }

    @Override
    public Conversation findById(Long id) {
        if(id == null){
            throw new IllegalArgumentException("id is null");
        }
        Conversation conversation = conversationDao.findById(id);

        if(conversation == null){
            throw new IllegalArgumentException("conversation is not found");
        }

        conversation.setConversationParticipants(
                conversation
                    .getConversationParticipants()
                    .stream()
                    .filter(participant -> participant.getLeftAt() == null)
                    .toList()
        );

        return conversation;
    }

    @Override
    public List<Conversation> findAll() {
        return conversationDao.findAll();
    }

}