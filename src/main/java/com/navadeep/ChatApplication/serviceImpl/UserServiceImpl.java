package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserLiteDao userLiteDao;
    private final AttachmentService attachmentService;
    private final Log log = LogFactory.getLog(UserServiceImpl.class);

    public UserServiceImpl(UserDao userDao, UserLiteDao userLiteDao,AttachmentService attachmentService) {
        this.userDao = userDao;
        this.userLiteDao = userLiteDao;
        this.attachmentService = attachmentService;
    }


    // unused method
    @Override
    public User save(User user) {
        return userDao.save(user);
    }

    @Override
    public User update(Long userId,String username,String displayName,String email, byte[] file, String fileName) {

        User user = userDao.findById(userId);

        if(username != null){
            UserLite existingUser = userLiteDao.findByUsername(username);
            if(existingUser != null && !existingUser.getId().equals(userId)){
                log.error("Username already exists for username: "+username);
                throw new RuntimeException("username "+username+" is already taken");
            }
            user.setUsername(username);
        }
        if(displayName != null){
            user.setDisplayName(displayName);
        }
        if(email != null){
            user.setEmail(email);
        }

        if(file != null && fileName != null){
            Attachment profileImageAttachment = attachmentService.save(fileName,file);
            user.setProfileImage(profileImageAttachment);
        }

        return userDao.update(user);
    }


    @Override
    public void delete(Long id) {
        if (id == null) {
            log.error("can't delete ID is null");
            throw new RuntimeException("ID cannot be null");
        }
        User user = userDao.findById(id);
        if(user == null){
            log.error("User : ["+id+"] not found");
            throw new RuntimeException("user with id "+id+" not found");
        }
        userDao.delete(user);
    }

    @Override
    public UserLite findById(Long id) {
        UserLite user = userLiteDao.findById(id);
        if(user == null){
            log.error("User : ["+id+"]not found");
            throw new RuntimeException("User with ID :  "+id+" not found");
        }
        return user;
    }

    @Override
    public List<UserLite> findAll() {
        return userLiteDao.findAll();
    }

    @Override
    public UserLite findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            log.error("username is null or empty");
            throw new RuntimeException("username cannot be null or empty");
        }
        return userLiteDao.findByUsername(username);
    }

    @Override
    public User getUserProfileById(Long id) {
        User user = userDao.findById(id);
        if(user == null){
            log.error("User: ["+id+"]not found");
            throw new RuntimeException("User with ID :  "+id+" not found");
        }
        return user;
    }
}