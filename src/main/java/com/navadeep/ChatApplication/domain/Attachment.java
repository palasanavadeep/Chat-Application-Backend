package com.navadeep.ChatApplication.domain;

import java.util.Arrays;

public class Attachment {
    private long id;
    private Lookup attachmentType;
    private byte[] file;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Lookup getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(Lookup attachmentType) {
        this.attachmentType = attachmentType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", attachmentType=" + attachmentType +
                ", file=" + Arrays.toString(file) +
                '}';
    }
}
