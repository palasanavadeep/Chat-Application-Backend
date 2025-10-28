package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface ConversationDao extends BaseDao<Conversation>{
    void addParticipant(Long conversationId, ConversationParticipant participant);
    void removeParticipant(Long conversationId, Long participantId);
    List<Conversation> findUserConversations(Long userId);
    List<ConversationParticipant> getAllParticipants(Long conversationId);
}
