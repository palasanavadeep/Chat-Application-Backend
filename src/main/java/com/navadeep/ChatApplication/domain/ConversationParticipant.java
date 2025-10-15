package com.navadeep.ChatApplication.domain;

import java.time.LocalDateTime;

public class ConversationParticipant {
    private long id;
    private Conversation conversation;
    private UserLite user;
    private Lookup role;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private boolean isMuted;
    private boolean isPinned;
    private Message lastReadMessage;
    private LocalDateTime lastReadAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
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

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
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

    public Message getLastReadMessage() {
        return lastReadMessage;
    }

    public void setLastReadMessage(Message lastReadMessage) {
        this.lastReadMessage = lastReadMessage;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
