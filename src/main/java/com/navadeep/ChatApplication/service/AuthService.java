package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.serviceImpl.AuthServiceImpl;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    AuthServiceImpl.AuthResponse login(String username, String password);
    AuthServiceImpl.AuthResponse register(User user,byte[] file,String fileName);
}
