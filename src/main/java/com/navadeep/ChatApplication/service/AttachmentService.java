package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.Attachment;

import java.util.List;

public interface AttachmentService extends BaseService<Attachment>{
    Attachment save(String fileName,byte[] file);
    Attachment update(Attachment attachment);
    void delete(Long id);
}