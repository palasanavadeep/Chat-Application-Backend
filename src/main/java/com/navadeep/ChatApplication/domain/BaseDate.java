package com.navadeep.ChatApplication.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;


@MappedSuperclass
public abstract class BaseDate extends PersistentObject {
    private LocalDateTime createdAt;

    public BaseDate() {}
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BaseDate{" +
                "createdAt=" + createdAt +
                '}'+ super.toString();
    }
}
