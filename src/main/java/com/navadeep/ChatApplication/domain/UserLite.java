package com.navadeep.ChatApplication.domain;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
@XmlRootElement(name = "userLite")
public class UserLite extends BaseDate {
    private String username;
    private String displayName;
    private Attachment profileImage;
    private boolean status;
    private LocalDateTime lastSeenAt;



    public UserLite(String username, String displayName, Attachment profileImage, boolean status, LocalDateTime lastSeenAt) {
        this.username = username;
        this.displayName = displayName;
        this.profileImage = profileImage;
        this.status = status;
        this.lastSeenAt = lastSeenAt;
    }

    public UserLite() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Attachment getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Attachment profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    @Override
    public String toString() {
        return "UserLite{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileImage=" + profileImage +
                ", status=" + status +
                ", lastSeenAt=" + lastSeenAt +
                '}'+super.toString();
    }
}
