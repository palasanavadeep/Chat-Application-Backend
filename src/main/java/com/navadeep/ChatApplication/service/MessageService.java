package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.Message;
import com.navadeep.ChatApplication.domain.UserLite;

import java.util.List;

public interface MessageService {
    Message sendMessage(Long senderId,Long conversationId,String messageContent,byte[] attachment,String attachmentName);
    Message editMessage(Long userId,Long messageId,String newMessageContent);
    void deleteMessageForMe(Long userId,Long messageId);
    void deleteMessageForEveryone(Long userId,Long messageId,Long conversationId);
}