package com.navadeep.ChatApplication.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final ConcurrentHashMap<String, ChannelHandlerContext> sessions = new ConcurrentHashMap<>();
    private ObjectMapper mapper;

    public void addSession(String userId, ChannelHandlerContext ctx) {
        sessions.put(userId, ctx);
        System.out.println("No of Active Users : "+sessions.size());
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void broadcast(WsResponse response, List<Long> receivers) {
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
            e.printStackTrace();
        }
    }


    public List<ChannelHandlerContext> getActiveSessionsForConversation(String convId) {
        List<String> participantIds =  new ArrayList<>();   /* Fetch from db */
        List<ChannelHandlerContext> active = new ArrayList<>();
        for (String id : participantIds) {
            ChannelHandlerContext ctx = sessions.get(id);
            if (ctx != null && ctx.channel().isActive()) {
                active.add(ctx);
            }
        }
        return active;
    }
}
