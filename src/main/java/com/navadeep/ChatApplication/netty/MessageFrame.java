package com.navadeep.ChatApplication.netty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageFrame {
    private String action;
    private Map<String, Object> data;
    private String file;
    private String fileName;

    // Getters & Setters
    public String getAction() { return action; }
    public void setAction(String a) { action = a; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> d) { data = d; }

    public String getFile() { return file; }
    public void setFile(String b) { file = b; }

    public String getFileName() { return fileName; }
    public void setFileName(String n) { fileName = n; }
}