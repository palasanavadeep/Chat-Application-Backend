package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.MessageReceipt;

import java.util.List;

public interface MessageReceiptService {
    MessageReceipt save(MessageReceipt messageReceipt);
    MessageReceipt update(MessageReceipt messageReceipt);
    void saveOrUpdateMessageReceipts(List<MessageReceipt> messageReceipts);
    List<Object[]> findMessageReceiptsByUserIdAndMessageIds(Long userId,List<Long> messageIds);
    MessageReceipt findByUserIdAndMessageId(Long userId,Long messageId);
    List<MessageReceipt> findByMessageId(Long messageId);

    int markMessagesInConversationAsRead(Long userId,Long conversationId);
    int markMessageAsRead(Long userId,Long messageId);
}
