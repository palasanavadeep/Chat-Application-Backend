package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@XmlRootElement(name = "conversation")
public class Conversation extends  BaseDate{

    private Lookup type; // PERSONAL / GROUP
    // for group conversation
    private String name;
    private String description;
    private Attachment conversationImage;
    private UserLite createdBy;
    private List<ConversationParticipant> conversationParticipants;
    private Message lastMessage;

    private boolean hasUnreadMessages;

    public boolean isHasUnreadMessages() {
        return hasUnreadMessages;
    }

    public void setHasUnreadMessages(boolean hasUnreadMessages) {
        this.hasUnreadMessages = hasUnreadMessages;
    }

    public Conversation() {}

    public Lookup getType() {
        return type;
    }

    public void setType(Lookup type) {
        this.type = type;
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

    public List<ConversationParticipant> getConversationParticipants() {
        return conversationParticipants;
    }

    public void setConversationParticipants(List<ConversationParticipant> conversationParticipants) {
        this.conversationParticipants = conversationParticipants;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
