package com.navadeep.ChatApplication.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String sayHello() {
        return "Hello World";
    }

    public String sayBye() {
        return "Bye World";
    }
}
