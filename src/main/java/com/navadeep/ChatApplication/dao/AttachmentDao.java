package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Attachment;
import java.util.List;

public interface AttachmentDao {
    Attachment save(Attachment attachment);
    Attachment update(Attachment attachment);
    void delete(Long id);
    Attachment findById(Long id);
    List<Attachment> findAll();
}