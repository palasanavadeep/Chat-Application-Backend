package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.*;

import java.util.List;

public interface ConversationService extends BaseService<Conversation>{


    Conversation createConversation(Long userId, String type,
                                    String name, String description, List<Long> participants, Attachment conversationImage);

    Conversation updateConversation(Long userId,Conversation conversation);
    void updateLastMessage(Long conversationId,Message message);
    List<Conversation> getUserConversations(Long userid);
    ConversationParticipant addParticipant(Long userId,Long newUserId,Long conversationId);
    void removeParticipant(Long userId,Long removedUserId,Long conversationId);
    List<ConversationParticipant> getAllParticipants(Long conversationId);
    void leaveConversation(Long userId,Long conversationId);

}