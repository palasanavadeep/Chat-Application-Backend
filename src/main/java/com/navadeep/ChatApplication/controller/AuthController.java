package com.navadeep.ChatApplication.controller;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.service.AuthService;
import com.navadeep.ChatApplication.service.UserService;
import com.navadeep.ChatApplication.serviceImpl.AuthServiceImpl;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Path;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import java.io.IOException;


@Path("/auth")
@CrossOriginResourceSharing(
        allowOrigins = {"http://localhost:3000"},
        allowCredentials = true,
        maxAge = 3600 // Cache preflight response for 1 hour
)
public class AuthController {

    private AuthService authService;
    private UserService userService;


    public AuthController(AuthService authService,UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(User user){
        System.out.println("in signin controller");

        if(user.getUsername() == null || user.getPassword() == null ){
            throw new RuntimeException("Username  and password can't be NULL");
        }

        AuthServiceImpl.AuthResponse userResponse = authService.login(user.getUsername(),user.getPassword());

        Response r = Response.ok(userResponse).build();
        System.out.println("Completed signin for "+ user.getUsername());
        return r;
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
            @Multipart("profileImageFile") Attachment profileImageFile,
            @Multipart("username") String username,
            @Multipart("email") String email,
            @Multipart("password") String password,
            @Multipart("displayName") String displayName
    ) throws IOException {

        if(username == null || password == null || email == null ){
            throw new RuntimeException("Username  and password and Email can't be NULL");
        }

        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPassword(password);
        user.setEmail(email);

        byte[] file = null;
        String fileName = null;
        if(profileImageFile != null){
            file = IOUtils.toByteArray(profileImageFile.getDataHandler().getInputStream());
            // Extract filename
            fileName = profileImageFile.getContentDisposition().getParameter("filename");

        }

        AuthServiceImpl.AuthResponse registeredUser = authService.register(user,file,fileName);

        return Response.ok(registeredUser)
                .build();
    }


    @GET
    @Path("/get/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(@PathParam("userId") Long userId){

        if (userId == null) {
            throw new RuntimeException("UserId can't be NULL");
        }

        System.out.println("in get User method");
        User user = userService.getUserProfileById(userId);

        System.out.println("User found User : "+user);

        return Response.ok(user).build();
    }


}

