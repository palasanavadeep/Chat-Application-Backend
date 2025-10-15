package com.navadeep.ChatApplication.controller;

import com.navadeep.ChatApplication.service.HelloService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class Hello {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "helloService.sayHello()" + "default";
    }

    @GET
    @Path("/bye")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayBye() {
        return "helloService.sayBye()";
    }
}
