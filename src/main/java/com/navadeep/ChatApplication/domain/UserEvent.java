package com.navadeep.ChatApplication.domain;

public class UserEvent {
    private long id;
    private UserLite user;
    private Message message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserLite getUser() {
        return user;
    }

    public void setUser(UserLite user) {
        this.user = user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
