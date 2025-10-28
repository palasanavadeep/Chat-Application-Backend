package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.*;
import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.service.ConversationParticipantService;
import com.navadeep.ChatApplication.service.ConversationService;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.service.MessageReceiptService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ConversationServiceImpl implements ConversationService {

    private ConversationDao conversationDao;
    private ConversationParticipantDao conversationParticipantDao;
    private MessageReceiptService messageReceiptService;
    private UserLiteDao userLiteDao;
    private ConversationParticipantService conversationParticipantService;
    private LookupService lookupService;

    public ConversationServiceImpl(ConversationDao conversationDao, ConversationParticipantDao conversationParticipantDao, MessageReceiptService messageReceiptService, UserLiteDao userLiteDao, ConversationParticipantService conversationParticipantService, LookupService lookupService) {
        this.conversationDao = conversationDao;
        this.conversationParticipantDao = conversationParticipantDao;
        this.messageReceiptService = messageReceiptService;
        this.userLiteDao = userLiteDao;
        this.conversationParticipantService = conversationParticipantService;
        this.lookupService = lookupService;
    }

    @Override
    public Conversation createConversation(Long userId, String type, String name, String description, List<Long> participants,Attachment conversationImage) {

        if(type == null || type.isEmpty()){
            throw new IllegalArgumentException("type is null or empty");
        }

        UserLite creator = userLiteDao.findById(userId);

        Conversation newConversation = new Conversation();
        newConversation.setCreatedBy(creator);
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation.setType(lookupService.findByLookupCode(type));



        System.out.println("conversation created for creator: " + creator);


        // create participant for creator of the conversation
        ConversationParticipant creatorParticipant = generateConversationParticipant(userId,"ADMIN");

        System.out.println("conversation participated created for creator: " + creator);

        // add members into participants list
        List<ConversationParticipant> conversationParticipants = new ArrayList<>();
        conversationParticipants.add(creatorParticipant);

        for(Long participantId : participants){
            conversationParticipants
                    .add(generateConversationParticipant(participantId,"MEMBER"));
        }

        if(type.equalsIgnoreCase("GROUP")){
            if(name == null || name.isEmpty()){
                throw new IllegalArgumentException("name is null or empty");
            }
            newConversation.setName(name);
            if(!description.isEmpty()){
                newConversation.setDescription(description);
            }
            if(conversationImage != null && conversationImage.getId() != null){
                newConversation.setConversationImage(conversationImage);
            }

        }

        if(conversationImage != null){
            newConversation.setConversationImage(conversationImage);
        }

        // save the new conversation
        Conversation createdConversation = conversationDao.save(newConversation);

        // broadcast this new conversation message to all participants (socket)

        return createdConversation;
    }

    private ConversationParticipant generateConversationParticipant(Long userId,String role){
        UserLite user =  userLiteDao.findById(userId);
        ConversationParticipant conversationParticipant = new ConversationParticipant();
        conversationParticipant.setUser(user);
        conversationParticipant.setIsMuted(false);
        conversationParticipant.setIsPinned(false);
        conversationParticipant.setCreatedAt(LocalDateTime.now());
        conversationParticipant.setRole(lookupService.findByLookupCode(role.toUpperCase()));
        return conversationParticipant;
    }

    @Override
    public Conversation updateConversation(Long userId,Conversation conversation) {

        if(conversation == null || conversation.getId() == null){
            throw new IllegalArgumentException("conversation is null or empty");
        }

        ConversationParticipant checkIfParticipant = conversationParticipantService.findByConversationAndUserId(conversation.getId(),userId);
        if(checkIfParticipant == null){
            throw new IllegalArgumentException("You are not allowed to update this conversation");
        }

        Conversation conversationToUpdate = conversationDao.findById(conversation.getId());
        conversationToUpdate.setName(conversation.getName());
        conversationToUpdate.setDescription(conversation.getDescription());
//        conversationToUpdate.setConversationImage(conversation.getConversationImage());

        return conversationDao.update(conversationToUpdate);
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
        conversationDao.save(conversation);
    }

    /**
     * Get all conversations of a user.
     * If personal -> fetch participants.
     * For group -> only include user’s participant info.
     */
    @Override
    public List<Conversation> getUserConversations(Long userId) {
        // Step 1: Fetch user conversations
        List<Conversation> conversations = conversationDao.findUserConversations(userId);

        if (conversations.isEmpty()) return conversations;

        // Step 2: Collect last message IDs
        List<Long> lastMessageIds = conversations.stream()
                .map(Conversation::getLastMessage)
                .filter(Objects::nonNull)
                .map(Message::getId)
                .toList();

        if (lastMessageIds.isEmpty()) {
            conversations.forEach(c -> c.setHasUnreadMessages(false));
            return conversations;
        }

        // Step 3: Fetch message receipts in batch
        List<Object[]> receiptResults = messageReceiptService
                .findMessageReceiptsByUserIdAndMessageIds(userId, lastMessageIds);

        Map<Long, Boolean> hasUnreadMap = receiptResults.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> {
                            String status = (String) row[1];
                            return !"READ".equalsIgnoreCase(status) && !"DELETED".equalsIgnoreCase(status);
                        }
                ));


        // Step 4: Map computed values and participants
        for (Conversation conv : conversations) {
            // Compute unread flag
            Message lastMsg = conv.getLastMessage();
            boolean hasUnread = lastMsg != null && hasUnreadMap.getOrDefault(lastMsg.getId(), false);
            conv.setHasUnreadMessages(hasUnread);

//            // Filter participants
//            if ("PERSONAL".equals(conv.getType().getLookupCode())) {
//                // Include both participants
//                List<UserLite> participants = conv.getConversationParticipants().stream()
//                        .map(ConversationParticipant::getUser)
//                        .collect(Collectors.toList());
//                conv.setParticipants(participants);
//            } else {
//                // Include only self for GROUP
//                conv.getConversationParticipants().stream()
//                        .filter(cp -> cp.getUser().getId().equals(userId))
//                        .findFirst()
//                        .ifPresent(cp -> conv.setParticipants(List.of(cp.getUser())));
//            }

            // filter participants
            if(!conv.getType().getLookupCode().equals("PERSONAL")){
                conv.getConversationParticipants().removeIf(cp -> !cp.getUser().getId().equals(userId));
            }
        }

        return conversations;
    }

    @Override
    public ConversationParticipant addParticipant(Long userId,Long newUserId,Long conversationId) {
        if(newUserId==null || userId==null || conversationId==null){
            throw new IllegalArgumentException("newUserId or conversationId is null");
        }

        ConversationParticipant conversationParticipant = conversationParticipantService
                .findByConversationAndUserId(conversationId,userId);

        if(conversationParticipant == null){
            throw new IllegalArgumentException("User : "+userId+" is not in the conversation"+conversationId);
        }

        if(!conversationParticipant.getRole().getLookupCode().equals("ADMIN")){
            throw new RuntimeException("UserId: "+userId+" not allowed to add participant to this conversation");
        }

        ConversationParticipant checkIfParticipant = conversationParticipantService.findByConversationAndUserId(conversationId,newUserId);

        if(checkIfParticipant == null){
            ConversationParticipant newConversationParticipant =
                    generateConversationParticipant(newUserId,"MEMBER");

            conversationDao.addParticipant(conversationId,newConversationParticipant);

            return newConversationParticipant;
        }
        else{
            checkIfParticipant.setLeftAt(null);
            return  conversationParticipantDao.update(checkIfParticipant);
        }



        // broadcast to newUserId with new Conversation


    }

    @Override
    public void removeParticipant(Long userId,Long participantId,Long conversationId) {

        ConversationParticipant conversationParticipant = conversationParticipantService.findByConversationAndUserId(conversationId,userId);

        if(conversationParticipant == null){
            throw new IllegalArgumentException("User : "+userId+" is not in the conversation"+conversationId);
        }

        if(!conversationParticipant.getRole().getLookupCode().equals("ADMIN")){
            throw new RuntimeException("UserId: "+userId+" not allowed to remove participant from this conversation");
        }

        ConversationParticipant checkIfParticipant = conversationParticipantService.findById(participantId);
        if(checkIfParticipant != null){
            conversationDao.removeParticipant(conversationId,participantId);
        }

        // broadcast message to checkIfParticipant.getUser().getId()
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
            participant.setLeftAt(LocalDateTime.now());
            conversationParticipantDao.update(participant);

            // broadcast to userId
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
        return conversation;
    }

    @Override
    public List<Conversation> findAll() {
        return conversationDao.findAll();
    }

}