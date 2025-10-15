package com.navadeep.ChatApplication.domain;

import java.time.LocalDateTime;

public class Message {
    private long id;
    private UserLite sender;
    private Conversation conversation;
    private String body;
    private Attachment messageAttachment;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private boolean deleteForEveryone;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserLite getSender() {
        return sender;
    }

    public void setSender(UserLite sender) {
        this.sender = sender;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Attachment getMessageAttachment() {
        return messageAttachment;
    }

    public void setMessageAttachment(Attachment messageAttachment) {
        this.messageAttachment = messageAttachment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public boolean isDeleteForEveryone() {
        return deleteForEveryone;
    }

    public void setDeleteForEveryone(boolean deleteForEveryone) {
        this.deleteForEveryone = deleteForEveryone;
    }
}
