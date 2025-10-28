package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement(name = "attachment")
public class Attachment extends PersistentObject {

    private Lookup attachmentType;
    private byte[] file;  // BLOB

    public Attachment() {}
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

}
