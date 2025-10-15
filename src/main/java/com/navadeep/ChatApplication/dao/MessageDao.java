package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.Message;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface MessageDao {
    Message save(Message message);
    Message update(Message message);
    void delete(Long id);
    Message findById(Long id);
    List<Message> findAll();
    List<Message> findByConversation(Conversation conversation);
    List<Message> findBySender(UserLite sender);
}