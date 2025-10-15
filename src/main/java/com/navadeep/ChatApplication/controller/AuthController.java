package com.navadeep.ChatApplication.controller;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

@Path("/auth")
public class AuthController {

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(){
        System.out.println("login");
        UserLite user = new UserLite();
        user.setUsername("admin");
        user.setId(12);
        user.setDisplayName("admin");
        user.setStatus(true);
        user.setLastSeenAt(LocalDateTime.now());
        user.setProfileImage(null);
        Response r = Response.ok(user).build();
        System.out.println(r.toString());
        return r;
    }


    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserLite register(@RequestBody User user){
        return new UserLite();
    }
}

