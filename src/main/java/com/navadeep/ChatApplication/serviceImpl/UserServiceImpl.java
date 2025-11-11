package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserLiteDao userLiteDao;
    private AttachmentService attachmentService;
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserDao userDao, UserLiteDao userLiteDao,AttachmentService attachmentService) {
        this.userDao = userDao;
        this.userLiteDao = userLiteDao;
        this.attachmentService = attachmentService;
    }


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

        User updatedUser = userDao.update(user);
        if(updatedUser == null){
            throw new RuntimeException("something went wrong");
        }
        return updatedUser;
    }


    @Override
    public void delete(Long id) {

        User user = userDao.findById(id);
        if(user == null){
            log.warn("User : {} not found",id);
            throw new RuntimeException("user with id "+id+" not found");
        }
        userDao.delete(user);
    }

    @Override
    public UserLite findById(Long id) {
        UserLite user = userLiteDao.findById(id);
        if(user == null){
            log.warn("User :{} not found",id);
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
        return userLiteDao.findByUsername(username);
    }

    @Override
    public User getUserProfileById(Long id) {
        User user = userDao.findById(id);
        if(user == null){
            log.warn("User: {} not found",id);
            throw new RuntimeException("User with ID :  "+id+" not found");
        }
        return user;
    }
}