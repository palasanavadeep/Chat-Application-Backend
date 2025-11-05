package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.AttachmentDao;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.LookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentDao attachmentDao;
    private final LookupService lookupService;

    Logger log =  LoggerFactory.getLogger(AttachmentServiceImpl.class);

    public AttachmentServiceImpl(AttachmentDao attachmentDao,LookupService lookupService) {
        this.attachmentDao = attachmentDao;
        this.lookupService = lookupService;
    }

    @Override
    public Attachment save(String fileName,byte[] file) {
        if(file==null || file.length==0){
            log.warn("file not found ");
            throw new RuntimeException("file is null or empty");
        }
        String fileType = getFileType(fileName);
        Attachment attachment = new Attachment();
        attachment.setAttachmentType(lookupService.findByLookupCode(fileType));
        attachment.setFile(file);
        return attachmentDao.save(attachment);
    }

    @Override
    public Attachment update(Attachment attachment) {
        if (attachment == null || attachment.getId() == null) {
            log.warn("attachment or attachmentId is null");
            throw new IllegalArgumentException("Attachment and ID cannot be null");
        }
        return attachmentDao.update(attachment);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            log.warn("can't delete ID is null");
            throw new IllegalArgumentException("ID cannot be null");
        }
        Attachment attachment = attachmentDao.findById(id);
        if(attachment == null) {
            log.warn("attachment with id {} not found ",id);
            throw new IllegalArgumentException("Attachment not found for Id :"+id);
        }
        attachmentDao.delete(attachment);
    }

    @Override
    public Attachment findById(Long id) {
        if (id == null) {
            log.warn("id is null");
            throw new IllegalArgumentException("ID cannot be null");
        }
        return attachmentDao.findById(id);
    }

    @Override
    public List<Attachment> findAll() {
        try {
            return attachmentDao.findAll();
        } catch (Exception e) {
            log.error("Error Message : {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find all attachments", e);
        }
    }


    // Gives the Attachment Type Code for Lookups
    public static String getFileType(String fileName) {
        // Extract file extension
        if(fileName==null || fileName.isEmpty()){
            return "OTHER";
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();

        // Define file type categories
        if (fileExtension.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return "IMAGE";
        } else if (fileExtension.equals("pdf")) {
            return "PDF";
        } else if (fileExtension.matches("mp4|mkv|avi|mov|flv|wmv|webm")) {
            return "VIDEO";
        } else if (fileExtension.matches("mp3|wav|ogg|flac|aac|m4a")) {
            return "AUDIO";
        } else {
            return "OTHER";  // For unsupported file types
        }
    }

    // Helper function to extract the file extension
    private static String getFileExtension(String fileName) {
        // Ensure the file name is not null or empty
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";  // No extension found
        }
        return fileName.substring(lastDotIndex + 1);
    }
}