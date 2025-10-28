package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.Message;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface MessageDao extends BaseDao<Message>{
    List<Message> findByConversationId(Long userId,Long conversationId);
    List<Message> findBySenderId(Long senderId);
}