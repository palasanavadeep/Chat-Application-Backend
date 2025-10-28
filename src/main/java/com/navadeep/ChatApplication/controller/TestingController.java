package com.navadeep.ChatApplication.controller;


import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.service.ConversationService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.ArrayList;
import java.util.List;

@Path("/test")
public class TestingController {

    private ConversationService conversationService;

    public TestingController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @POST
    @Path("/addConversation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(@RequestBody User user){
        List<Long> members = new ArrayList<>();
        members.add(1L);
        Conversation conversation =     conversationService
                .createConversation(user.getId(),
                        "PERSONAL",
                        "","",
                        members,null);

        return Response.ok(conversation).build();
    }
}
