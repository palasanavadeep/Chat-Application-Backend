package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.MessageReceipt;

import java.util.List;

public interface MessageReceiptDao extends BaseDao<MessageReceipt>{
    List<MessageReceipt> findByMessageId(Long messageId);
    void saveOrUpdateAll(List<MessageReceipt> receipts);
    List<Object[]> findByUserIdAndMessageIds(Long userId,List<Long> messageIds);
    MessageReceipt findByUserIdAndMessageId(Long userId, Long messageId);
}
