package com.navadeep.ChatApplication.domain;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseDate extends PersistentObject {
    private Long createdAt;

    public BaseDate() {}
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BaseDate{" +
                "createdAt=" + createdAt +
                '}'+ super.toString();
    }
}
