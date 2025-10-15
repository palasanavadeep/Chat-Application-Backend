package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface ConversationParticipantDao {
    ConversationParticipant save(ConversationParticipant participant);
    ConversationParticipant update(ConversationParticipant participant);
    void delete(Long id);
    ConversationParticipant findById(Long id);
    List<ConversationParticipant> findAll();
    List<ConversationParticipant> findByConversation(Conversation conversation);
    ConversationParticipant findByConversationAndUser(Conversation conversation, UserLite user);
}
