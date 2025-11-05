package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "conversationParticipant")
public class ConversationParticipant extends BaseDate{
    private Long conversationId;
    private UserLite user;
    private Lookup role;
    private Long leftAt;
    private boolean isMuted;
    private boolean isPinned;

    @Override
    public String toString() {
        return "ConversationParticipant{" +
                "conversationId=" + conversationId +
                "user=" + user +
                ", role=" + role +
                ", leftAt=" + leftAt +
                ", isMuted=" + isMuted +
                ", isPinned=" + isPinned +
                '}';
    }

    public ConversationParticipant() {}

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public UserLite getUser() {
        return user;
    }

    public void setUser(UserLite user) {
        this.user = user;
    }

    public Lookup getRole() {
        return role;
    }

    public void setRole(Lookup role) {
        this.role = role;
    }

    public Long getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Long leftAt) {
        this.leftAt = leftAt;
    }

    public boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(boolean pinned) {
        isPinned = pinned;
    }
}
