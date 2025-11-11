package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;

import java.util.List;

public interface UserService {
    User save(User user); //unused

    User update(Long userId,String username,String displayName,String email,byte[] file,String fileName); // todo
    void delete(Long id);
    UserLite findById(Long id);
    List<UserLite> findAll();
    UserLite findByUsername(String username);
    User getUserProfileById(Long id);
}