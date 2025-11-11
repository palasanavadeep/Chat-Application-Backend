package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.*;

import java.util.List;

public interface ConversationService extends BaseService<Conversation>{


    Conversation createConversation(Long userId, String type,
                                    String name, String description, List<Long> participants,byte[] conversationImageFile,String fileName);

    Conversation updateConversation(Long userId,Long conversationId,String name,String description,byte[] conversationImageFile,String fileName);
    void updateLastMessage(Long conversationId,Message message);
    List<Conversation> getUserConversations(Long userid);
    ConversationParticipant addParticipant(Long userId,Long newUserId,Long conversationId);
    void removeParticipant(Long userId,Long participantId);
    List<ConversationParticipant> getAllParticipants(Long conversationId);
    void leaveConversation(Long userId,Long conversationId);

}