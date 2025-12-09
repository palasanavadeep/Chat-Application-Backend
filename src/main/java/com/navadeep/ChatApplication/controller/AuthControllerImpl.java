package com.navadeep.ChatApplication.controller;

import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.service.AuthService;
import com.navadeep.ChatApplication.test.LoadTestDataGenerator;
import com.navadeep.ChatApplication.utils.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;


public class AuthControllerImpl implements  AuthController{

    private final AuthService authService;
    private final LoadTestDataGenerator loadTestDataGenerator;

    private final Log log = LogFactory.getLog(AuthControllerImpl.class.getName());

    public AuthControllerImpl(AuthService authService,LoadTestDataGenerator loadTestDataGenerator) {
        this.authService = authService;
        this.loadTestDataGenerator = loadTestDataGenerator;
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

            log.info("User {"+user.getUsername()+"} logged in successfully ");
            return Response.ok(userResponse).build();
        }catch (Exception e){
            log.error("Login error : "+e.getMessage());
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

            log.info("User: "+user+" registered successfully");

            return Response.ok(registeredUser)
                    .build();

        }catch (Exception e){
            log.error("Registration error :"+e.getMessage(),e);
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

    @POST
    @Path("/generate")
    public void loadTestData(){
        loadTestDataGenerator.generate();
    }

}

