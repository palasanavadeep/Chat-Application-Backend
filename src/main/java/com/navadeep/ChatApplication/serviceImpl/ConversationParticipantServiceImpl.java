package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.ConversationParticipantDao;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.exception.BadRequestException;
import com.navadeep.ChatApplication.exception.ForbiddenException;
import com.navadeep.ChatApplication.exception.NotFoundException;
import com.navadeep.ChatApplication.exception.UnauthorizedException;
import com.navadeep.ChatApplication.netty.SessionManager;
import com.navadeep.ChatApplication.netty.WsResponse;
import com.navadeep.ChatApplication.service.ConversationParticipantService;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.utils.Constants;
import com.navadeep.ChatApplication.utils.WS_ACTION;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.util.Map;

public class ConversationParticipantServiceImpl implements ConversationParticipantService {

    private final ConversationParticipantDao conversationParticipantDao;
    private final LookupService lookupService;
    private final SessionManager sessionManager;

    private final Log log =  LogFactory.getLog(ConversationParticipantServiceImpl.class);

    public ConversationParticipantServiceImpl(
            ConversationParticipantDao conversationParticipantDao,
            LookupService lookupService,
            SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.conversationParticipantDao = conversationParticipantDao;
        this.lookupService = lookupService;
    }

    @Override
    public ConversationParticipant update(Long userId,Long participantId,Boolean isMuted,Boolean isPinned) {
        if(userId == null || participantId == null){
            log.error("userId :["+userId+"] or participantId : ["+participantId+"] can't be null");
            throw new BadRequestException("userId or participant is null");
        }

        ConversationParticipant conversationParticipant = conversationParticipantDao.findById(participantId);

        if(!conversationParticipant.getUser().getId().equals(userId)){
            log.error("userId :["+userId+"] can't update participantId : "+participantId);
            throw new ForbiddenException("You are not allowed to update this participant.");
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
            log.error("userId :["+userId+"] or participantId : ["+participantId+"]is NULL ");
            throw new BadRequestException("userId or participantId or role or conversationId is null");
        }

        ConversationParticipant participantToUpdate = conversationParticipantDao.findById(participantId);
        if(participantToUpdate == null){
            log.error("participantId : ["+participantId+"] is NOT FOUND");
            throw  new NotFoundException("Participant to update not found.");
        }

        Long conversationId = participantToUpdate.getConversationId();

        ConversationParticipant isAdminParticipant = conversationParticipantDao.getParticipantByConversationIdAndUserId(conversationId, userId);

        if(isAdminParticipant == null || !isAdminParticipant.getRole().getLookupCode().equals(Constants.ROLE_ADMIN)){
            log.error("userId :["+userId+"] or is NOT ADMIN");
            throw new ForbiddenException("You are not allowed to update the role for a participant.");
        }

        participantToUpdate.setRole(lookupService.findByLookupCode(role));

        ConversationParticipant updatedParticipant =  conversationParticipantDao.update(participantToUpdate);

        // to notify all the existing participants about updated role
        List<Long> participantUserIds = this.findParticipantUserIdsByConversationId(conversationId);
        WsResponse removedParticipantResponse = WsResponse.success(WS_ACTION.UPDATE_PARTICIPANT,
                Map.of("conversationId",conversationId,
                        "participant",updatedParticipant));
        sessionManager.broadcast(removedParticipantResponse,participantUserIds);

        return updatedParticipant;

    }

    @Override
    public ConversationParticipant findByConversationAndUserId(Long conversationId, Long userId) {
        if(conversationId == null || userId == null){
            log.error("conversationId :["+conversationId+"] or userId : ["+userId+"] is NULL ");
            throw new BadRequestException("conversationId or userId is null");
        }
        return  conversationParticipantDao
                .getParticipantByConversationIdAndUserId(conversationId, userId);
    }

    @Override
    public List<Long> findParticipantUserIdsByConversationId(Long conversationId) {
        if(conversationId == null){
            log.error("conversationId  is NULL ");
            throw new BadRequestException("conversationId is null");
        }
        return conversationParticipantDao.findParticipantUserIdsByConversationId(conversationId);
    }


    @Override
    public ConversationParticipant findById(Long id) {
        if(id == null){
            log.error("id is NULL ");
            throw new BadRequestException("id is null");
        }
        ConversationParticipant conversationParticipant = conversationParticipantDao.findById(id);
        if(conversationParticipant == null){
            log.error("ConversationParticipant Not Found");
            throw new NotFoundException("ConversationParticipant not found");
        }

        return conversationParticipant;
    }

    @Override
    public List<ConversationParticipant> findAll() {
        return conversationParticipantDao.findAll();
    }
}