package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.UserEvent;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;

public interface UserEventDao {
    UserEvent save(UserEvent userEvent);
    UserEvent update(UserEvent userEvent);
    void delete(Long id);
    UserEvent findById(Long id);
    List<UserEvent> findAll();
    List<UserEvent> findByUser(UserLite user);
}