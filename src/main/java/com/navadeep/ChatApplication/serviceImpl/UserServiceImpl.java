package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.UserService;
import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserLiteDao userLiteDao;
    private AttachmentService attachmentService;

    public UserServiceImpl(UserDao userDao, UserLiteDao userLiteDao,AttachmentService attachmentService) {
        this.userDao = userDao;
        this.userLiteDao = userLiteDao;
        this.attachmentService = attachmentService;
    }


    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User update(User user, byte[] file, String fileName) {
        if(user.getId()==null){
            throw new RuntimeException("User id is null, can't update user");
        }

        if(user.getPassword().length() < 6){
            throw new RuntimeException("password length is less than 6");
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
            throw new RuntimeException("user with id "+id+" not found");
        }
        userDao.delete(user);
        System.out.println("user with id "+id+" deleted");

    }

    @Override
    public UserLite findById(Long id) {
        UserLite user = userLiteDao.findById(id);
        if(user == null){
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
            throw new RuntimeException("User with ID :  "+id+" not found");
        }
        return user;
    }
}