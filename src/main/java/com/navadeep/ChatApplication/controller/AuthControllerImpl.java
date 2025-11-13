package com.navadeep.ChatApplication.controller;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.service.AuthService;
import com.navadeep.ChatApplication.utils.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthControllerImpl implements  AuthController{

    private final AuthService authService;

    private static final Logger log =  LoggerFactory.getLogger(AuthControllerImpl.class);

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response login(User user){
        try{
            if(user.getUsername() == null || user.getPassword() == null ){
                log.error("username or password is NULL");
                throw new RuntimeException("Username  and password can't be NULL");
            }

            ApiResponse userResponse = authService
                    .login(user.getUsername(),user.getPassword());

            log.info("User {} logged in successfully",user.getUsername());
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


    @Override
    public Response register(
            @Multipart("profileImageFile") Attachment profileImageFile,
            @Multipart("user") User user
    ){

        try{
            if(user.getUsername() == null || user.getPassword() == null || user.getEmail() == null ){
                throw new RuntimeException("Username  and password and Email can't be NULL");
            }


            byte[] file = null;
            String fileName = null;
            if(profileImageFile != null){
                file = IOUtils.toByteArray(profileImageFile.getDataHandler().getInputStream());
                // Extract filename
                fileName = profileImageFile.getContentDisposition().getParameter("filename");

            }


            ApiResponse registeredUser = authService.register(user,file,fileName);

            log.info("User {} registered successfully",user);
            System.out.println("registered user : "+registeredUser);

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

}

