package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.daoImpl.UserDaoImpl;
import com.navadeep.ChatApplication.daoImpl.UserLiteDaoImpl;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.AuthService;
import com.navadeep.ChatApplication.utils.JwtUtil;
import com.navadeep.ChatApplication.utils.ApiResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;


public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final UserLiteDao userLiteDao;
    private final AttachmentService attachmentService;
    private final JwtUtil jwtUtil;

    Log log = LogFactory.getLog(AuthServiceImpl.class);

    AuthServiceImpl(UserDaoImpl userDao, UserLiteDaoImpl userLiteDao,AttachmentServiceImpl attachmentService, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.userLiteDao = userLiteDao;
        this.attachmentService = attachmentService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ApiResponse login(String username, String password) {

        User user = userDao.findByUsername(username);
        if(user == null){
            log.error("User with username not found"+username);
            throw new RuntimeException("user with "+username+" not found");
        }

        if(!user.getPassword().equals(password)){
            log.error("Password Mismatch for User {}"+username);
            throw new RuntimeException("Credentials didn't match! Try again..");
        }

        String token = jwtUtil.generateToken(user.getId().toString());  // generate token from JWT util.

        log.info("User ["+username+"] logged in successfully");
        return new ApiResponse(
                true,
                "Login Successful",
                Map.of("token",token,"user",user));
    }

    @Override
    public ApiResponse register(User user,byte[] file,String fileName) {

        if(user.getPassword().length() < 6){
            log.error("Weak Password for User {}"+user.getUsername());
            throw new RuntimeException("password length is less than 6");
        }
        // check if username already exist
        UserLite checkUsername = userLiteDao.findByUsername(user.getUsername());
        if(checkUsername != null){
            log.error("User with Username {} already exists"+user.getUsername());
            throw new RuntimeException("username already is exist");
        }

        if(file != null && fileName != null){
            Attachment profileImageAttachment = attachmentService.save(fileName,file);
            user.setProfileImage(profileImageAttachment);
        }

        user.setCreatedAt(System.currentTimeMillis());
        user.setLastSeenAt(System.currentTimeMillis());
        user.setStatus(true);

        User newRegisteredUser = userDao.save(user);
        if(newRegisteredUser == null){
            throw new RuntimeException("something went wrong");
        }

        String token = jwtUtil.generateToken(user.getId().toString());

        return new ApiResponse(
                true,
                "Registration Successful",
                Map.of("token",token,"user",newRegisteredUser)
        );

    }

    @Override
    public void logout(Long userId) {
        UserLite user = userLiteDao.findById(userId);
        if(user == null){
            log.error("User with id {} not found"+userId);
            throw new RuntimeException("user not found");
        }
        user.setLastSeenAt(System.currentTimeMillis());
        user.setStatus(false);

        userLiteDao.update(user);
    }

}
