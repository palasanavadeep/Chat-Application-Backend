package com.navadeep.ChatApplication.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navadeep.ChatApplication.utils.Constants;
import com.navadeep.ChatApplication.utils.JwtUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class ChatWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");
    Log log = LogFactory.getLog(ChatWebSocketHandler.class);

    private final SessionManager sessionManager;
    private final ObjectMapper mapper;
    private final JwtUtil jwtUtil;
    private final Dispatcher dispatcher;
    private final ExecutorService executor;

    private final StringBuilder frameBuffer = new StringBuilder(); // Buffer for fragmented frames


    public ChatWebSocketHandler(ApplicationContext springContext) {
        this.sessionManager = (SessionManager) springContext.getBean("sessionManager");
        this.mapper = (ObjectMapper) springContext.getBean("objectMapper");
        this.jwtUtil = (JwtUtil) springContext.getBean("jwtUtil");
        this.dispatcher = (Dispatcher) springContext.getBean("dispatcher");
        this.executor = (ExecutorService) springContext.getBean("workerThreadExecutor");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
//        log.info("Received data: " + msg.toString());
        if (msg instanceof FullHttpRequest) {
            handleHandshake(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
//            log.info("Received WebSocket frame: " + msg.toString());
            handleFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.uri().startsWith(Constants.WS_SERVER_ENDPOINT)) {
            ctx.close();
            return;
        }

        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(
                        getWebSocketURL(req),
                        null,
                        true,
                        20 * 1024 * 1024 // max frame size 20 MB
                );

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
        sessionManager.addSession(userId, ctx);

//        log.info("WebSocket handshake successful for userId: " + userId);
    }

    private void handleFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // Handle Text Frames (can be fragmented)
        if (frame instanceof TextWebSocketFrame) {
            frameBuffer.append(((TextWebSocketFrame) frame).text());

            if (frame.isFinalFragment()) {
                processCompleteMessage(ctx, frameBuffer.toString());
                frameBuffer.setLength(0); // clear buffer
            }
        }
        // Handle Continuation Frames
        else if (frame instanceof ContinuationWebSocketFrame) {
            frameBuffer.append(((ContinuationWebSocketFrame) frame).text());

            if (frame.isFinalFragment()) {
                processCompleteMessage(ctx, frameBuffer.toString());
                frameBuffer.setLength(0);
            }
        }
        // Handle Ping/Pong/Close frames
        else if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        } else {
            throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
        }
    }

    private void processCompleteMessage(ChannelHandlerContext ctx, String json) {
        executor.submit(()->{
            try {
                MessageFrame msg = mapper.readValue(json, MessageFrame.class);
                Long userId = Long.parseLong(ctx.channel().attr(USER_ID_KEY).get());
                dispatcher.dispatch(userId, msg);
            } catch (Exception e) {
                log.error("JSON parse error: {}"+e.getMessage(), e);
                WsResponse response = WsResponse.error("ERROR", "Invalid JSON or too large payload");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        });
    }

    private String getWebSocketURL(FullHttpRequest req) {
        return "ws://" + req.headers().get("Host") + Constants.WS_SERVER_ENDPOINT;
    }

    private void sendHttpResponse(ChannelHandlerContext ctx,
                                  HttpResponseStatus status,
                                  String jsonContent) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(jsonContent, StandardCharsets.UTF_8)
        );
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes())
                .set(HttpHeaderNames.CONNECTION, "close");
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
        log.error("Exception caught: "+cause.getMessage(), cause);
        WsResponse response = WsResponse.error(Constants.STATUS_ERROR, cause.getMessage());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }
}
