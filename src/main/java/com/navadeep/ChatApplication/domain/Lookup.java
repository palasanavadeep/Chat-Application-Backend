package com.navadeep.ChatApplication.domain;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement(name = "lookup")
public class Lookup extends PersistentObject {
    private String lookupName;
    private String lookupCategory;
    private String lookupCode;

    public Lookup() {}
    public String getLookupName() {
        return lookupName;
    }

    public void setLookupName(String lookupName) {
        this.lookupName = lookupName;
    }

    public String getLookupCategory() {
        return lookupCategory;
    }

    public void setLookupCategory(String lookupCategory) {
        this.lookupCategory = lookupCategory;
    }

    public String getLookupCode() {
        return lookupCode;
    }

    public void setLookupCode(String lookupCode) {
        this.lookupCode = lookupCode;
    }

    @Override
    public String toString() {
        return "Lookup{" +
                "lookupName='" + lookupName + '\'' +
                ", lookupCategory='" + lookupCategory + '\'' +
                ", lookupCode='" + lookupCode + '\'' +
                '}';
    }
}
