package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.ConversationParticipantDao;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.service.ConversationParticipantService;
import com.navadeep.ChatApplication.service.LookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class ConversationParticipantServiceImpl implements ConversationParticipantService {

    private final ConversationParticipantDao conversationParticipantDao;
    private final LookupService lookupService;

    private final Logger log =  LoggerFactory.getLogger(ConversationParticipantServiceImpl.class);

    public ConversationParticipantServiceImpl(
            ConversationParticipantDao conversationParticipantDao,
            LookupService lookupService) {
        this.conversationParticipantDao = conversationParticipantDao;
        this.lookupService = lookupService;
    }

    // for mute ,pinned
    @Override
    public ConversationParticipant update(Long userId,Long participantId,Boolean isMuted,Boolean isPinned) {
        if(userId == null || participantId == null){
            log.warn("userId :{} or participantId : {} can't be null",userId,participantId);
            throw new NullPointerException("userId or participant is null");
        }

        ConversationParticipant conversationParticipant = conversationParticipantDao.findById(participantId);

        if(!conversationParticipant.getUser().getId().equals(userId)){
            log.warn("userId :{} can't update participantId : {} ",userId,participantId);
            throw new IllegalArgumentException("You are not allowed to update this participant.");
        }

        if(isMuted != null){
            conversationParticipant.setIsMuted(isMuted);
        }
        if (isPinned != null){
            conversationParticipant.setIsPinned(isPinned);
        }

        return conversationParticipantDao.update(conversationParticipant);

    }

    // REVIEW (BL)
    @Override
    public ConversationParticipant updateParticipantRole(Long userId,Long participantId,Long conversationId,String role) {
        if(userId == null || participantId == null || role == null || conversationId == null){
            log.warn("userId :{} or participantId : {} is NULL ",userId,participantId);
            throw new NullPointerException("userId or participantId or role or conversationId is null");
        }

        ConversationParticipant isAdminParticipant = conversationParticipantDao.getParticipantByConversationIdAndUserId(conversationId, userId);

        if(isAdminParticipant == null || !isAdminParticipant.getRole().getLookupCode().equals("ADMIN")){
            log.warn("userId :{} or participantId : {} is NOT ADMIN",userId,participantId);
            throw new IllegalArgumentException("You are not allowed to update the role for a participant.");
        }

        ConversationParticipant conversationParticipant = conversationParticipantDao.findById(participantId);
        conversationParticipant.setRole(lookupService.findByLookupCode(role));

        ConversationParticipant updatedParticipant =  conversationParticipantDao.update(conversationParticipant);

        // broadcast message to participant (later)

        return updatedParticipant;

    }

    @Override
    public ConversationParticipant findByConversationAndUserId(Long conversationId, Long userId) {
        ConversationParticipant conversationParticipant = conversationParticipantDao
                .getParticipantByConversationIdAndUserId(conversationId, userId);

        if(conversationParticipant == null){
            log.warn("conversationId is NULL");
            throw new IllegalArgumentException("ConversationParticipant Not Found");
        }

        return conversationParticipant;
    }

    @Override
    public List<Long> findParticipantUserIdsByConversationId(Long conversationId) {
        return conversationParticipantDao.findParticipantUserIdsByConversationId(conversationId);
    }


    @Override
    public ConversationParticipant findById(Long id) {
        ConversationParticipant conversationParticipant = conversationParticipantDao.findById(id);
        if(conversationParticipant == null){
            log.warn("ConversationParticipant Not Found");
            throw new IllegalArgumentException("ConversationParticipant not found");
        }

        return conversationParticipant;
    }

    @Override
    public List<ConversationParticipant> findAll() {
        return conversationParticipantDao.findAll();
    }
}