package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
public class Message extends BaseDate {
    private UserLite sender;
    private Long conversationId;
    private String body;
    private Attachment attachment;
    private Long editedAt;


    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", conversationId=" + conversationId +
                ", body='" + body + '\'' +
                ", attachment=" + attachment +
                ", editedAt=" + editedAt +
                '}';
    }

    public Message() {}
    public UserLite getSender() {
        return sender;
    }

    public void setSender(UserLite sender) {
        this.sender = sender;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Long editedAt) {
        this.editedAt = editedAt;
    }
}
