package com.navadeep.ChatApplication.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Conversation {
    private long id;
    private Lookup type;
    // for group conversation
    private String name;
    private String description;
    private Attachment conversationImage;

    private UserLite createdBy;
    private LocalDateTime createdAt;
    private List<ConversationParticipant> members;
    private Message lastMessage;

    public Lookup getType() {
        return type;
    }

    public void setType(Lookup type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Attachment getConversationImage() {
        return conversationImage;
    }

    public void setConversationImage(Attachment conversationImage) {
        this.conversationImage = conversationImage;
    }

    public UserLite getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserLite createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ConversationParticipant> getMembers() {
        return members;
    }

    public void setMembers(List<ConversationParticipant> members) {
        this.members = members;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
