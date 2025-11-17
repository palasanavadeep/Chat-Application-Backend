package com.navadeep.ChatApplication.serviceImpl;


import com.navadeep.ChatApplication.dao.AttachmentDao;
import com.navadeep.ChatApplication.domain.Attachment;
import com.navadeep.ChatApplication.exception.BadRequestException;
import com.navadeep.ChatApplication.exception.InternalServerException;
import com.navadeep.ChatApplication.exception.NotFoundException;
import com.navadeep.ChatApplication.service.AttachmentService;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;


public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentDao attachmentDao;
    private final LookupService lookupService;

    Log log =  LogFactory.getLog(AttachmentServiceImpl.class);

    public AttachmentServiceImpl(AttachmentDao attachmentDao,LookupService lookupService) {
        this.attachmentDao = attachmentDao;
        this.lookupService = lookupService;
    }

    @Override
    public Attachment save(String fileName,byte[] file) {
        if(file==null || file.length==0){
            log.error("file not found ");
            throw new BadRequestException("file is null or empty");
        }
        String fileType = getFileType(fileName);
        Attachment attachment = new Attachment();
        attachment.setAttachmentType(lookupService.findByLookupCode(fileType));
        attachment.setFile(file);

        try{
            return attachmentDao.save(attachment);
        }catch (Exception e){
            log.error("Failed to save attachment: " + e.getMessage(), e);
            throw new InternalServerException("Failed to save attachment");
        }
    }

    @Override
    public Attachment update(Attachment attachment) {
        if (attachment == null || attachment.getId() == null) {
            log.error("attachment or attachmentId is null");
            throw new BadRequestException("Attachment and ID cannot be null");
        }
        try{
            return attachmentDao.update(attachment);
        }catch (Exception e){
            log.error("Failed to update attachment: " + e.getMessage(), e);
            throw new InternalServerException("Failed to update attachment");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            log.error("can't delete ID is null");
            throw new BadRequestException("ID cannot be null");
        }
        Attachment attachment = attachmentDao.findById(id);
        if(attachment == null) {
            log.error("attachment with id {} not found "+id);
            throw new NotFoundException("Attachment not found for Id :"+id);
        }
        try{
            attachmentDao.delete(attachment);
        }
        catch (Exception e){
            log.error("Failed to delete attachment: " + e.getMessage(), e);
            throw new InternalServerException("Failed to delete attachment");
        }
    }

    @Override
    public Attachment findById(Long id) {
        if (id == null) {
            log.error("id is null");
            throw new BadRequestException("ID cannot be null");
        }
        Attachment attachment = attachmentDao.findById(id);
        if (attachment == null) {
            log.error("Attachment not found: " + id);
            throw new NotFoundException("Attachment not found for ID: " + id);
        }

        return attachment;
    }

    @Override
    public List<Attachment> findAll() {
        try {
            return attachmentDao.findAll();
        } catch (Exception e) {
            log.error("Failed to fetch attachments: " + e.getMessage(), e);
            throw new InternalServerException("Failed to fetch attachments");
        }
    }


    // Gives the Attachment Type Code for Lookups
    private static String getFileType(String fileName) {
        // Extract file extension
        if(fileName==null || fileName.isEmpty()){
            return Constants.ATTACHMENT_TYPE_FILE;
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();

        // Define file type categories
        if (fileExtension.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return Constants.ATTACHMENT_TYPE_IMAGE;
        } else if (fileExtension.equals("pdf")) {
            return Constants.ATTACHMENT_TYPE_FILE;
        } else if (fileExtension.matches("mp4|mkv|avi|mov|flv|wmv|webm")) {
            return Constants.ATTACHMENT_TYPE_FILE;
        } else if (fileExtension.matches("mp3|wav|ogg|flac|aac|m4a")) {
            return Constants.ATTACHMENT_TYPE_FILE;
        } else {
            return Constants.ATTACHMENT_TYPE_FILE;  // For unsupported file types
        }
    }

    // Helper function :: Extracts file extension from filename
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? "" : fileName.substring(lastDot + 1);
    }
}