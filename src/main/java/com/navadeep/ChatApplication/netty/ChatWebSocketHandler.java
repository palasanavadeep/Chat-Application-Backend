package com.navadeep.ChatApplication.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navadeep.ChatApplication.domain.*;
import com.navadeep.ChatApplication.service.*;
import com.navadeep.ChatApplication.utils.JwtUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import org.springframework.context.ApplicationContext;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");

    private final SessionManager sessionManager;
    private final ObjectMapper mapper;
    private JwtUtil jwtUtil;

    // services
    private AttachmentService attachmentService;
    private ConversationService conversationService;
    private ConversationParticipantService conversationParticipantService;
    private MessageService messageService;
    private MessageReceiptService messageReceiptService;
    private UserService userService;

    public ChatWebSocketHandler(ApplicationContext springContext) {
        this.sessionManager = (SessionManager) springContext.getBean("sessionManager");
        this.mapper = (ObjectMapper) springContext.getBean("objectMapper");
        this.jwtUtil = (JwtUtil) springContext.getBean("jwtUtil");

        this.attachmentService = (AttachmentService) springContext.getBean("attachmentServiceImpl");
        this.conversationService = (ConversationService) springContext.getBean("conversationServiceImpl");
        this.conversationParticipantService = (ConversationParticipantService) springContext.getBean("conversationParticipantServiceImpl");
        this.messageService = (MessageService) springContext.getBean("messageServiceImpl");
        this.messageReceiptService = (MessageReceiptService) springContext.getBean("messageReceiptServiceImpl");
        this.userService = (UserService) springContext.getBean("userServiceImpl");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHandshake(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.uri().startsWith("/ws")) {
            ctx.close(); // Reject non-WS
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }

        // Validate JWT
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        String token = decoder.parameters().get("token") != null ? decoder.parameters().get("token").get(0) : null;
        String userId = jwtUtil.validateToken(token);
        if (userId == null) {
            sendHttpResponse(ctx, HttpResponseStatus.UNAUTHORIZED, "{\"error\": \"Invalid JWT\"}");
            ctx.close();
            return;
        }

        System.out.println(userId);


        handshaker.handshake(ctx.channel(), req);
        ctx.channel().attr(USER_ID_KEY).set(userId);
        sessionManager.addSession(userId, ctx); // Add to active sessions
    }

    private void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws JsonProcessingException {
        if (frame instanceof TextWebSocketFrame) {
            String json = ((TextWebSocketFrame) frame).text();
            MessageFrame msg = mapper.readValue(json, MessageFrame.class);
            Long userId = Long.parseLong(ctx.channel().attr(USER_ID_KEY).get());


            switch (msg.getAction()) {
                case "sendMessage" -> { // in service
                    System.out.println("hello");

                    byte[] file = getFile(msg);
                    String fileName = getFileName(msg);
                    System.out.println("fileName ; "+fileName);

                    Map<String, Object> data = msg.getData();

                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
                    String messageContent = data.get("messageContent") != null ? data.get("messageContent").toString() : null;

                    System.out.println("messageContent : "+messageContent);
                    try{
                        if(conversationId != null){
                            messageService.sendMessage(userId,conversationId,messageContent,file,fileName);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }



                }
                case "editMessage" -> {

                    Long messageId = Long.parseLong(msg.getData().get("messageId").toString());
                    String messageContent = msg.getData().get("messageContent").toString();

                    messageService.editMessage(userId,messageId,messageContent);
                }
                case "deleteMessageForMe" -> {

                    Long messageId = Long.parseLong(msg.getData().get("messageId").toString());

                    messageService.deleteMessageForMe(userId,messageId);
                }
                case "deleteMessageForEveryone" -> {

                    Long messageId = Long.parseLong(msg.getData().get("messageId").toString());
                    Long conversationId = Long.parseLong(msg.getData().get("conversationId").toString());

                    messageService.deleteMessageForEveryone(userId,messageId,conversationId);
                }
                case "createNewConversation" -> {

                    Map<String, Object> data = msg.getData();
                    System.out.println("in createNewConversation");

                    String type = data.get("type").toString();
                    String name = (data.get("name") != null) ? data.get("name").toString() : null;
                    String description = (data.get("description") != null) ? data.get("description").toString() : null;

                    List<Long> participants = new ArrayList<>();
                    if (data.get("participants") instanceof List<?> list) {
                        list.forEach(p -> participants.add(Long.parseLong(p.toString())));
                    }

                    byte[] conversationImageFile = getFile(msg);
                    String fileName = getFileName(msg);

                    conversationService
                            .createConversation(userId,type,name,description,participants,conversationImageFile,fileName);

                    System.out.println("in createNewConversation end");

                }
                case "updateConversation" -> {

                    Map<String, Object> data = msg.getData();
                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
                    String name = (data.get("name") != null) ? data.get("name").toString() : null;
                    String description = (data.get("description") != null) ? data.get("description").toString() : null;

                    byte[] conversationImageFile = getFile(msg);
                    String fileName = getFileName(msg);

                    conversationService.updateConversation(userId,conversationId,name,description,conversationImageFile,fileName);

                }
                case "addUserToConversation" -> {
                    Map<String, Object> data = msg.getData();
                    Long newUserId = data.get("newUserId") != null ? Long.parseLong(data.get("newUserId").toString()) : null;
                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

                    conversationService.addParticipant(userId,newUserId,conversationId);

                }
                case "removeUserFromConversation" -> {

                    Map<String, Object> data = msg.getData();
                    Long removedUserId = data.get("removedUserId") != null ? Long.parseLong(data.get("removedUserId").toString()) : null;
                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

                    conversationService.removeParticipant(userId,removedUserId,conversationId);
                }
                case "updateParticipantRole" -> {

                    Map<String, Object> data = msg.getData();
                    Long participantId = data.get("participantId") != null ? Long.parseLong(data.get("participantId").toString()) : null;
                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
                    String role = (data.get("role") != null) ? data.get("role").toString() : null;

                    conversationParticipantService.updateParticipantRole(userId,participantId,conversationId,role);
                }

                case "getConversationParticipants" -> {

                    Map<String, Object> data = msg.getData();

                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;
                    List<ConversationParticipant> participants = conversationService.getAllParticipants(conversationId);

                    WsResponse wsResponse = WsResponse.success("getConversationParticipantsResponse", Map.of("participants", participants,"conversationId", conversationId));
                    sessionManager.broadcast(wsResponse,List.of(userId));

                }
                case "getUserConversations" -> {

                    List<Conversation> conversations = conversationService.getUserConversations(userId);
                    WsResponse wsResponse = WsResponse.success("getUserConversationsResponse", conversations);
                    sessionManager.broadcast(wsResponse,List.of(userId));

                }
                case "getConversation" -> {

                    Map<String, Object> data = msg.getData();

                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

                    Conversation conversation = conversationService.findById(conversationId);

                    WsResponse wsResponse = WsResponse.success("getConversationResponse", conversation);
                    sessionManager.broadcast(wsResponse,List.of(userId));
                }
                case "getProfile" -> {

                    User user = userService.getUserProfileById(userId);
                    WsResponse wsResponse = WsResponse.success("getProfileResponse", user);
                    sessionManager.broadcast(wsResponse,List.of(userId));
                }
                case "getAllMessages" -> {
                    Map<String, Object> data = msg.getData();
                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

                    if(conversationId != null) {
                        List<Message> conversationMessages = messageService
                                .getMessageByConversationId(userId,conversationId);

                        WsResponse wsResponse = WsResponse.success("getAllMessagesResponse",
                                Map.of("conversationId" , conversationId,"messages", conversationMessages));
                        sessionManager.broadcast(wsResponse,List.of(userId));
                    }

                }
                case "markMessageAsRead" -> {
                    Map<String, Object> data = msg.getData();
                    Long messageId =  data.get("messageId") != null ? Long.parseLong(data.get("messageId").toString()) : null;

                    messageReceiptService.markMessageAsRead(userId,messageId);
                }

                case "markConversationMessagesAsRead" -> {
                    Map<String, Object> data = msg.getData();
                    Long conversationId = data.get("conversationId") != null ? Long.parseLong(data.get("conversationId").toString()) : null;

                    messageReceiptService.markMessagesInConversationAsRead(userId,conversationId);
                }

                case "searchUser"->{
                    Map<String, Object> data = msg.getData();
                    String username = data.get("username") != null ? data.get("username").toString() : null;
                    UserLite userResult= null;
                    if(username != null){
                        userResult = userService.findByUsername(username);
                    }
                    WsResponse wsResponse = WsResponse.success("searchUserResponse", List.of(userResult));
                    sessionManager.broadcast(wsResponse,List.of(userId));
                }

                default -> {
                    ctx.writeAndFlush(new TextWebSocketFrame("{\"error\": \"Invalid action\"}"));
                }
            }


        }
        // Handle BinaryWebSocketFrame for Large files
    }

    private byte[] getFile(MessageFrame msg) {
        if (msg.getFile() == null || msg.getFile().isBlank()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(msg.getFile());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Base64 file data");
            return null;
        }
    }
    private String getFileName(MessageFrame msg) {
        return (msg.getFileName() != null && !msg.getFileName().isBlank())
                ? msg.getFileName()
                : null;
    }


    private String getWebSocketURL(FullHttpRequest req) {
        return "ws://" + req.headers().get("Host") + "/ws";
    }

    private void sendHttpResponse(ChannelHandlerContext ctx,
                                  HttpResponseStatus status,
                                  String jsonContent) {
        // 1. Create response with JSON body
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(jsonContent, StandardCharsets.UTF_8)
        );

        // 2. Set required headers
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes())
                .set(HttpHeaderNames.CONNECTION, "close");  // Force close after response

        // 3. Write and flush + close channel
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userId = ctx.channel().attr(USER_ID_KEY).get();
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}