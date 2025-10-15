package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface ConversationDao {
    Conversation save(Conversation conversation);
    Conversation update(Conversation conversation);
    void delete(Long id);
    Conversation findById(Long id);
    List<Conversation> findAll();
    List<Conversation> findByUser(UserLite user);
}
