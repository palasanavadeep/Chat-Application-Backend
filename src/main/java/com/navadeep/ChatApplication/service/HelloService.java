package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.UserLite;
import com.navadeep.ChatApplication.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


@Service
public class HelloService {


    public static UserServiceImpl userService;

    @Autowired
    public ApplicationContext context;

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public String sayHello() {
        return "Hello World";
    }

    public String sayBye() {
        return "Bye World";
    }

    public static void main(String[] args) {




        UserLite newUser = new UserLite();
        newUser.setUsername("Navadeep");
        newUser.setStatus(true);
        newUser.setDisplayName("Navadeep");
        newUser.setLastSeenAt(System.currentTimeMillis());
        newUser.setProfileImage(null);




    }




}
