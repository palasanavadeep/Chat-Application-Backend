package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.daoImpl.UserDaoImpl;
import com.navadeep.ChatApplication.daoImpl.UserLiteDaoImpl;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.AuthService;
import com.navadeep.ChatApplication.utils.JwtUtil;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

public class AuthServiceImpl implements AuthService {

    private UserDao userDao;
    private UserLiteDao userLiteDao;  // remove useless
    private AttachmentService attachmentService;
    private JwtUtil jwtUtil;

    AuthServiceImpl(UserDaoImpl userDao, UserLiteDaoImpl userLiteDao,AttachmentServiceImpl attachmentService, JwtUtil jwtUtil) {
        this.userDao = (UserDaoImpl)userDao;
        this.userLiteDao = (UserLiteDao) userLiteDao;
        this.attachmentService = attachmentService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(String username, String password) {

        User user = userDao.findByUsername(username);
        if(user == null){
            throw new RuntimeException("user with "+username+" not found");
        }

        if(!user.getPassword().equals(password)){
            throw new RuntimeException("Credentials didn't match! Try again..");
        }

        String token = jwtUtil.generateToken(user.getId().toString());  // generate token from JWT util.

        return new AuthResponse(token,user);
    }

    @Override
    public AuthResponse register(User user,byte[] file,String fileName) {

        if(user.getPassword().length() < 6){
            throw new RuntimeException("password length is less than 6");
        }
        // check if username already exist
        UserLite checkUsername = userLiteDao.findByUsername(user.getUsername());
        if(checkUsername != null){
            throw new RuntimeException("username already is exist");
        }

        // todo :: hash password (next)

        if(file != null && fileName != null){
            Attachment profileImageAttachment = attachmentService.save(fileName,file);
            user.setProfileImage(profileImageAttachment);
        }

        user.setCreatedAt(LocalDateTime.now());

        User newRegisteredUser = userDao.save(user);
        if(newRegisteredUser == null){
            throw new RuntimeException("something went wrong");
        }



        String token = jwtUtil.generateToken(user.getId().toString());

        return new AuthResponse(token,newRegisteredUser);

    }

    public record AuthResponse(String token, UserLite user) {
    }
}
