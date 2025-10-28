package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.UserLite;

public interface UserLiteDao extends BaseDao<UserLite>{
    UserLite findByUsername(String username);
}
