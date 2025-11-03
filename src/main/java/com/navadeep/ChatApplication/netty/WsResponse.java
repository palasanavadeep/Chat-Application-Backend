package com.navadeep.ChatApplication.netty;



public class WsResponse {

    private final String action;
    private final String status;   // "success" or "error"
    private final Object data;
    private final String message;

    public WsResponse(String action, String status, Object data, String message) {
        this.action = action;
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static WsResponse success(String action, Object data) {
        return new WsResponse(action, "success", data, null);
    }

    public static WsResponse error(String action, String message) {
        return new WsResponse(action, "error", null, message);
    }

    public String getAction() {
        return action;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}