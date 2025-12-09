package com.navadeep.ChatApplication.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class SessionManager {

    Log log = LogFactory.getLog(SessionManager.class);
    private final ConcurrentHashMap<String, ChannelHandlerContext> sessions = new ConcurrentHashMap<>();
    private ObjectMapper mapper;
    private ExecutorService executor;


    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void addSession(String userId, ChannelHandlerContext ctx) {
        sessions.put(userId, ctx);
        log.info("User ["+ userId+"] added  No of Active Users : "+sessions.size());
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
        log.info("User ["+userId+"] removed  No of Active Users : "+sessions.size());
    }


    public void broadcast(WsResponse response, List<Long> receivers) {
        executor.submit(()->{
            try {
                String json = mapper.writeValueAsString(response);
                TextWebSocketFrame frame = new TextWebSocketFrame(json);

                for (Long receiver : receivers) {
                    ChannelHandlerContext ctx = sessions.get(receiver.toString());
                    if (ctx != null && ctx.channel().isActive()) {
                        ctx.writeAndFlush(frame.retainedDuplicate()); // Efficient broadcast
                    }
                }
                frame.release(); // release retained buffer
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });
    }
}
