package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.serviceImpl.AuthServiceImpl;
import com.navadeep.ChatApplication.utils.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    ApiResponse login(String username, String password);
    ApiResponse register(User user,byte[] file,String fileName);
    void logout(Long userId);
}
