package com.navadeep.ChatApplication.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement(name = "persistantobject")
@MappedSuperclass
public abstract class  PersistentObject implements Serializable {
    private Long id;
    public PersistentObject() {}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PersistentObject{" +
                "id=" + id +
                '}';
    }
}
