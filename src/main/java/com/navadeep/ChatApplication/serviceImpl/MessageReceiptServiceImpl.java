package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.MessageReceiptDao;
import com.navadeep.ChatApplication.daoImpl.MessageReceiptDaoImpl;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.domain.MessageReceipt;
import com.navadeep.ChatApplication.service.LookupService;
import com.navadeep.ChatApplication.service.MessageReceiptService;

import java.util.List;
import java.util.Objects;

public class MessageReceiptServiceImpl implements MessageReceiptService {
    private MessageReceiptDao messageReceiptDao;
    private LookupService lookupService;

    public MessageReceiptServiceImpl(MessageReceiptDao messageReceiptDao, LookupService lookupService) {
        this.messageReceiptDao = messageReceiptDao;
        this.lookupService = lookupService;
    }

    @Override
    public MessageReceipt save(MessageReceipt messageReceipt) {
        return messageReceiptDao.save(messageReceipt);
    }

    @Override
    public MessageReceipt update(MessageReceipt messageReceipt) {
        return messageReceiptDao.update(messageReceipt);
    }

    @Override
    public void saveOrUpdateMessageReceipts(List<MessageReceipt> messageReceipts) {
        messageReceiptDao.saveOrUpdateAll(messageReceipts);
    }

    @Override
    public List<Object[]> findMessageReceiptsByUserIdAndMessageIds(Long userId, List<Long> messageIds) {
        return messageReceiptDao.findByUserIdAndMessageIds(userId,messageIds);
    }

    @Override
    public MessageReceipt findByUserIdAndMessageId(Long userId, Long messageId) {
        return messageReceiptDao.findByUserIdAndMessageId(userId,messageId);
    }

    @Override
    public List<MessageReceipt> findByMessageId(Long messageId) {
        return messageReceiptDao.findByMessageId(messageId);
    }

    @Override
    public int markMessagesInConversationAsRead(Long userId, Long conversationId) {
        if (conversationId == null) {
            throw new RuntimeException("conversationId is null");
        }

        Lookup readStatus = lookupService.findByLookupCode("READ");
        return messageReceiptDao.markConversationAsRead(userId, conversationId, readStatus);
    }

    @Override
    public int markMessageAsRead(Long userId, Long messageId) {
        if (messageId == null) {
            throw new RuntimeException("messageId is null");
        }
        Lookup readStatus = lookupService.findByLookupCode("READ");
        return messageReceiptDao.markMessageAsRead(userId, messageId, readStatus);
    }


}
