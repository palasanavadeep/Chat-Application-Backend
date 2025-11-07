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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");

    private final SessionManager sessionManager;
    private final ObjectMapper mapper;
    private final JwtUtil jwtUtil;
    private final ChatEventHandler chatEventHandler;

    Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    public ChatWebSocketHandler(ApplicationContext springContext) {
        this.sessionManager = (SessionManager) springContext.getBean("sessionManager");
        this.mapper = (ObjectMapper) springContext.getBean("objectMapper");
        this.jwtUtil = (JwtUtil) springContext.getBean("jwtUtil");
        this.chatEventHandler = (ChatEventHandler) springContext.getBean("chatEventHandler");
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

        handshaker.handshake(ctx.channel(), req);
        ctx.channel().attr(USER_ID_KEY).set(userId);
        sessionManager.addSession(userId, ctx); // Add to active sessions

//        // Remove HTTP handlers from pipeline after handshake
//        ctx.pipeline().remove(HttpServerCodec.class);
//        ctx.pipeline().remove(HttpObjectAggregator.class);
    }

    private void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws JsonProcessingException {
        if (frame instanceof TextWebSocketFrame) {
            String json = ((TextWebSocketFrame) frame).text();
            MessageFrame msg = mapper.readValue(json, MessageFrame.class);
            Long userId = Long.parseLong(ctx.channel().attr(USER_ID_KEY).get());

            switch (msg.getAction()) {
                case "sendMessage" -> { // in service
                    chatEventHandler.sendMessageHandler(userId, msg);
                }
                case "editMessage" -> {
                    chatEventHandler.editMessageHandler(userId, msg);
                }
                case "deleteMessageForMe" -> {
                    chatEventHandler.deleteMessageForMeHandler(userId, msg);
                }
                case "deleteMessageForEveryone" -> {
                    chatEventHandler.deleteMessageForEveryoneHandler(userId, msg);
                }
                case "createNewConversation" -> {
                    chatEventHandler.createConversationHandler(userId, msg);
                }
                case "updateConversation" -> {
                    chatEventHandler.updateConversationHandler(userId, msg);
                }
                case "addUserToConversation" -> {
                    chatEventHandler.addUserToConversationHandler(userId, msg);
                }
                case "removeUserFromConversation" -> {
                    chatEventHandler.removeUserFromConversationHandler(userId, msg);
                }
                case "updateParticipantRole" -> {
                    chatEventHandler.updateParticipantRoleHandler(userId, msg);
                }
                case "getConversationParticipants" -> {
                    chatEventHandler.getConversationParticipantsHandler(userId, msg);
                }
                case "getUserConversations" -> {
                    chatEventHandler.getUserConversationsHandler(userId, msg);
                }
                case "getConversation" -> {
                    chatEventHandler.getConversationHandler(userId, msg);
                }
                case "getProfile" -> {
                    chatEventHandler.getProfileHandler(userId, msg);
                }
                case "getAllMessages" -> {
                    chatEventHandler.getAllMessagesHandler(userId, msg);
                }
                case "markMessageAsRead" -> {
                    chatEventHandler.markMessageAsReadHandler(userId, msg);
                }
                case "markConversationMessagesAsRead" -> {
                    chatEventHandler.markConversationMessagesAsReadHandler(userId, msg);
                }
                case "searchUser"->{
                    chatEventHandler.searchUserHandler(userId, msg);
                }
                default -> {
                    sessionManager.broadcast(
                            WsResponse.error("ERROR", "Invalid Socket action"),
                            List.of(userId)
                    );
                }
            }
        }
        // Handle BinaryWebSocketFrame for Large files
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

        log.error("Exception caught: {}",cause.getMessage());
        System.out.println("Exception caught:"+cause.getMessage());

        WsResponse response = WsResponse.error("ERROR",cause.getMessage());

        // Send the response back to the client
        ctx.writeAndFlush(response).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Error response sent successfully");
            } else {
                log.warn("Failed to send error response to the client");
            }
        });

        ctx.close();
    }

}