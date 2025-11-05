package com.navadeep.ChatApplication.dao;

import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.domain.MessageReceipt;

import java.util.List;

public interface MessageReceiptDao extends BaseDao<MessageReceipt>{
    List<MessageReceipt> findByMessageId(Long messageId);
    void saveOrUpdateAll(List<MessageReceipt> receipts);

    // for getting lastMessage receipt (computing hasUnreadMessagesInConversation)
    List<MessageReceipt> findByUserIdAndMessageIds(Long userId,List<Long> messageIds);

    MessageReceipt findByUserIdAndMessageId(Long userId, Long messageId);

    int markConversationAsRead(Long userId, Long conversationId, Lookup readStatus);
    int markMessageAsRead(Long userId, Long messageId, Lookup readStatus);
}
