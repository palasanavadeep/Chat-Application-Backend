package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement(name = "messageReceipt")
public class MessageReceipt extends PersistentObject {
    private Message message;
    private Long userId;
    private Lookup status;  // SENT,DELIVERED,READ,DELETED

    public MessageReceipt() {}
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Lookup getStatus() {
        return status;
    }

    public void setStatus(Lookup status) {
        this.status = status;
    }
}
