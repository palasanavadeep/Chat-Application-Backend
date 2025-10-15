package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import java.util.List;


public interface UserDao {
    UserLite save(UserLite user);
    UserLite update(UserLite user);
    void delete(Long id);
    UserLite findById(Long id);
    List<UserLite> findAll();
    UserLite findByUsername(String username);
}

//public interface UserDao {
//    public void createUser(User user);
//    public void updateUser(User user);
//    public void deleteUser(String id);
//    public User getUserById(String id);
//
//    // for search
//    public List<UserLite> getUsersByUsernameSubstring(String usernameSubstring);
//
//}
