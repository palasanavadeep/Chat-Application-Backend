package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;

import java.util.List;

public interface ConversationParticipantService extends BaseService<ConversationParticipant>{

    ConversationParticipant update(Long userId,Long participantId,Boolean isMuted,Boolean isPinned);
    ConversationParticipant updateParticipantRole(Long userId,Long participantId,Long conversationId,String role);
    ConversationParticipant findByConversationAndUserId(Long conversationId, Long userId);
    List<Long> findParticipantUserIdsByConversationId(Long conversationId);

}