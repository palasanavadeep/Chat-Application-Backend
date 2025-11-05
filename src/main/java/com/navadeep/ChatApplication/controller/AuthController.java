package com.navadeep.ChatApplication.controller;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.service.AuthService;
import com.navadeep.ChatApplication.service.UserService;
import com.navadeep.ChatApplication.utils.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    private static final Logger log =  LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService,UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(User user){
        try{
            if(user.getUsername() == null || user.getPassword() == null ){
                log.error("username or password is NULL");
                throw new RuntimeException("Username  and password can't be NULL");
            }

            ApiResponse userResponse = authService
                    .login(user.getUsername(),user.getPassword());


            return Response.ok(userResponse).build();
        }catch (Exception e){
            log.error("Login error : {}",e.getMessage());
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(
                            false,
                            e.getMessage(),
                            null))
                    .build();
        }
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
    ){

        try{
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

            ApiResponse registeredUser = authService.register(user,file,fileName);

            return Response.ok(registeredUser)
                    .build();
        }catch (Exception e){
            log.error("Registration error : {}",e.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(
                            false,
                            e.getMessage(),
                            null
                    ))
                    .build();
        }
    }


    @GET
    @Path("/get/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(@PathParam("userId") Long userId){
        try{
            if (userId == null) {
                throw new RuntimeException("UserId can't be NULL");
            }

            User user = userService.getUserProfileById(userId);

            return Response.ok(user).build();
        }catch (Exception e){
            log.error("Get User By UserId : {}",e.getMessage());
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(
                            false,
                            e.getMessage(),
                            null))
                    .build();
        }
    }



}

