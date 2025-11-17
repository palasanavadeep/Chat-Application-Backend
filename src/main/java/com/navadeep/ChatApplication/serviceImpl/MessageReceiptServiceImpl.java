package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.MessageReceiptDao;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.domain.MessageReceipt;
import com.navadeep.ChatApplication.exception.BadRequestException;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.service.MessageReceiptService;
import com.navadeep.ChatApplication.utils.MESSAGE_STATUS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class MessageReceiptServiceImpl implements MessageReceiptService {
    private final MessageReceiptDao messageReceiptDao;
    private final LookupService lookupService;
    Log log = LogFactory.getLog(MessageReceiptServiceImpl.class);

    public MessageReceiptServiceImpl(MessageReceiptDao messageReceiptDao, LookupService lookupService) {
        this.messageReceiptDao = messageReceiptDao;
        this.lookupService = lookupService;
    }

    @Override
    public MessageReceipt save(MessageReceipt messageReceipt) {
        if (messageReceipt == null) {
            log.error("messageReceipt is null");
            throw new BadRequestException("messageReceipt is null");
        } else {
            return messageReceiptDao.save(messageReceipt);
        }
    }

    @Override
    public MessageReceipt update(MessageReceipt messageReceipt) {
        if (messageReceipt == null) {
            log.error("messageReceipt is null");
            throw new BadRequestException("messageReceipt is null");
        } else {
            return messageReceiptDao.update(messageReceipt);
        }
    }

    @Override
    public void saveOrUpdateMessageReceipts(List<MessageReceipt> messageReceipts) {
        if (messageReceipts == null || messageReceipts.isEmpty()) {
            log.error("messageReceipts is null or empty");
            throw new BadRequestException("messageReceipts is null or empty");
        } else {
            messageReceiptDao.saveOrUpdateAll(messageReceipts);
        }
    }

    @Override
    public List<MessageReceipt> findMessageReceiptsByUserIdAndMessageIds(Long userId, List<Long> messageIds) {
        if(userId == null){
            log.error("userId is null");
            throw new BadRequestException("userId is null");
        }
        if(messageIds == null || messageIds.isEmpty()) {
            log.error("messageIds is null or empty");
            throw new BadRequestException("messageIds is null or empty");
        }
        return messageReceiptDao.findByUserIdAndMessageIds(userId,messageIds);
    }

    @Override
    public MessageReceipt findByUserIdAndMessageId(Long userId, Long messageId) {
        if(userId == null && messageId == null){
            log.error("userId and messageId are null");
            throw new BadRequestException("userId and messageId are null");
        }
        else {
            return messageReceiptDao.findByUserIdAndMessageId(userId, messageId);
        }
    }

    @Override
    public List<MessageReceipt> findByMessageId(Long messageId) {
        if(messageId == null){
            log.error("messageId is null");
            throw new BadRequestException("messageId is null");
        }
        else {
            return messageReceiptDao.findByMessageId(messageId);
        }
    }

    @Override
    public int markMessagesInConversationAsRead(Long userId, Long conversationId) {
        if (conversationId == null) {
            throw new BadRequestException("conversationId is null");
        }
        Lookup readStatus = lookupService.findByLookupCode(MESSAGE_STATUS.READ);
        return messageReceiptDao.markConversationAsRead(userId, conversationId, readStatus);
    }

    @Override
    public int markMessageAsRead(Long userId, Long messageId) {
        if (messageId == null) {
            throw new BadRequestException("messageId is null");
        }
        Lookup readStatus = lookupService.findByLookupCode(MESSAGE_STATUS.READ);
        return messageReceiptDao.markMessageAsRead(userId, messageId, readStatus);
    }


}
