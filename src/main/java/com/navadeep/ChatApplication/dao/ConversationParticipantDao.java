package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface ConversationParticipantDao extends BaseDao<ConversationParticipant> {
    List<Long> findParticipantUserIdsByConversationId(Long conversationId);
    ConversationParticipant getParticipantByConversationIdAndUserId(Long conversationId,Long userId);
}
