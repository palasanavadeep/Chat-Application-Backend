package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.ConversationParticipantDao;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.netty.SessionManager;
import com.navadeep.ChatApplication.netty.WsResponse;
import com.navadeep.ChatApplication.service.ConversationParticipantService;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

public class ConversationParticipantServiceImpl implements ConversationParticipantService {

    private final ConversationParticipantDao conversationParticipantDao;
    private final LookupService lookupService;
    private final SessionManager sessionManager;

    private final Logger log =  LoggerFactory.getLogger(ConversationParticipantServiceImpl.class);

    public ConversationParticipantServiceImpl(
            ConversationParticipantDao conversationParticipantDao,
            LookupService lookupService,
            SessionManager sessionManager) {
        this.sessionManager = sessionManager;
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
    public ConversationParticipant updateParticipantRole(Long userId,Long participantId,String role) {

        if(userId == null || participantId == null || role == null){
            log.warn("userId :{} or participantId : {} is NULL ",userId,participantId);
            throw new NullPointerException("userId or participantId or role or conversationId is null");
        }

        ConversationParticipant participantToUpdate = conversationParticipantDao.findById(participantId);
        if(participantToUpdate == null){
            log.warn("participantId : {} is NOT FOUND",participantId);
            throw  new IllegalArgumentException("Participant to update not found.");
        }

        Long conversationId = participantToUpdate.getConversationId();

        ConversationParticipant isAdminParticipant = conversationParticipantDao.getParticipantByConversationIdAndUserId(conversationId, userId);

        if(isAdminParticipant == null || !isAdminParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            log.warn("userId :{} or participantId : {} is NOT ADMIN",userId,participantId);
            throw new IllegalArgumentException("You are not allowed to update the role for a participant.");
        }

        participantToUpdate.setRole(lookupService.findByLookupCode(role));

        ConversationParticipant updatedParticipant =  conversationParticipantDao.update(participantToUpdate);

        // to notify all the existing participants about updated role
        List<Long> participantUserIds = this.findParticipantUserIdsByConversationId(conversationId);
        WsResponse removedParticipantResponse = WsResponse.success(Constants.WS_ACTION_UPDATE_PARTICIPANT,
                Map.of("conversationId",conversationId,
                        "participant",updatedParticipant));
        sessionManager.broadcast(removedParticipantResponse,participantUserIds);

        return updatedParticipant;

    }

    @Override
    public ConversationParticipant findByConversationAndUserId(Long conversationId, Long userId) {
        return  conversationParticipantDao
                    .getParticipantByConversationIdAndUserId(conversationId, userId);
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