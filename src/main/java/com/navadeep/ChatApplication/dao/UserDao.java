package com.navadeep.ChatApplication.dao;


import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;

public interface UserDao  extends BaseDao<User>{
    User findByEmail(String email);

    User findByUsername(String username);
}
