package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;

@XmlRootElement(name = "conversationParticipant")
public class ConversationParticipant extends BaseDate{

    private UserLite user;
    private Lookup role;
    private LocalDateTime leftAt;
    private boolean isMuted;
    private boolean isPinned;

    public ConversationParticipant() {}
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

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
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
