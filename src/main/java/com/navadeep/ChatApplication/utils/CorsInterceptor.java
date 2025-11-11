package com.navadeep.ChatApplication.utils;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.*;

public class CorsInterceptor extends AbstractPhaseInterceptor<Message> {

    private String allowOrigin = "*";
    private boolean allowCredentials = false;
    private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
    private String allowedHeaders = "Origin,Content-Type,Accept,Authorization,Content-Disposition,X-Requested-With";
    private String maxAge = "3600";

    public CorsInterceptor() {
        super(Phase.MARSHAL);
    }

    // XML-configurable setters
    public void setAllowOrigin(String allowOrigin) { this.allowOrigin = allowOrigin; }
    public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }
    public void setAllowedMethods(String allowedMethods) { this.allowedMethods = allowedMethods; }
    public void setAllowedHeaders(String allowedHeaders) { this.allowedHeaders = allowedHeaders; }
    public void setMaxAge(String maxAge) { this.maxAge = maxAge; }

    @Override
    public void handleMessage(Message message) throws Fault {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        if (headers == null) {
            headers = new HashMap<>();
            message.put(Message.PROTOCOL_HEADERS, headers);
        }

        headers.put("Access-Control-Allow-Origin",  Collections.singletonList(allowOrigin));
        headers.put("Access-Control-Allow-Methods", Collections.singletonList(allowedMethods));
        headers.put("Access-Control-Allow-Headers", Collections.singletonList(allowedHeaders));
        headers.put("Access-Control-Max-Age",       Collections.singletonList(maxAge));
        if (allowCredentials) {
            headers.put("Access-Control-Allow-Credentials", Collections.singletonList("true"));
        }else{
            headers.put("Access-Control-Allow-Credentials", Collections.singletonList("false"));
        }

        if ("OPTIONS".equalsIgnoreCase((String) message.get(Message.HTTP_REQUEST_METHOD))) {
            message.getExchange().put(Message.RESPONSE_CODE, 200);
            message.getInterceptorChain().abort();
        }
    }
}