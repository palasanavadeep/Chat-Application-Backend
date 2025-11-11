package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.MessageDao;
import com.navadeep.ChatApplication.domain.Message;
import com.navadeep.ChatApplication.domain.MessageReceipt;
import jakarta.persistence.criteria.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.Collections;
import java.util.List;

public class MessageDaoImpl extends BaseDaoImpl<Message> implements MessageDao {

    public MessageDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Message.class);
    }


//    @Override
//    public List<Message> findByConversationId(Long userId,Long conversationId) {
//        try (Session session = sessionFactory.openSession()) {
//
////            CriteriaBuilder cb = session.getCriteriaBuilder();
////            CriteriaQuery<Message> cq = cb.createQuery(Message.class);
////            Root<Message> messageRoot = cq.from(Message.class);
////
////            // Join with MessageReceipt
////            Join<Message, MessageReceipt> receiptJoin = messageRoot.join("messageReceipts", JoinType.INNER);
////
////            cq.select(messageRoot)
////                    .distinct(true)
////                    .where(
////                            cb.and(
////                                    cb.equal(messageRoot.get("conversationId"), conversationId),
////                                    cb.equal(receiptJoin.get("userId"), userId),
////                                    cb.notEqual(receiptJoin.get("status").get("lookupCode"), "DELETED")
////                            )
////                    )
////                    .orderBy(cb.asc(messageRoot.get("createdAt")));
////
////            return session.createQuery(cq).getResultList();
//            CriteriaBuilder cb = session.getCriteriaBuilder();
//            CriteriaQuery<Message> cq = cb.createQuery(Message.class);
//            Root<Message> m = cq.from(Message.class);
//
//// Subquery to filter messages having valid receipts
//            Subquery<Long> sub = cq.subquery(Long.class);
//            Root<MessageReceipt> mr = sub.from(MessageReceipt.class);
//            sub.select(mr.get("message").get("id"))
//                    .where(
//                            cb.and(
//                                    cb.equal(mr.get("userId"), userId),
//                                    cb.equal(mr.get("message").get("conversationId"), conversationId),
//                                    cb.notEqual(mr.get("status").get("lookupCode"), "DELETED")
//                            )
//                    );
//
//            cq.select(m)
//                    .where(m.get("id").in(sub))
//                    .orderBy(cb.asc(m.get("createdAt")));
//
//            return session.createQuery(cq).getResultList();
//
//
//
//        } catch (HibernateException e) {
//            log.error("Error Message ; {}",e.getMessage(),e);
//            return Collections.emptyList();
//        }
//    }



    // future versions

    @Override
    public List<Message> findByConversationId(Long userId, Long conversationId) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Message> cq = cb.createQuery(Message.class);

            // Root for Message
            Root<Message> messageRoot = cq.from(Message.class);

            // Subquery to find valid message IDs
            Subquery<Long> sub = cq.subquery(Long.class);
            Root<MessageReceipt> mr = sub.from(MessageReceipt.class);

            // Subquery selects message IDs that meet conditions
            sub.select(mr.get("message").get("id"))
                    .where(
                            cb.and(
                                    cb.equal(mr.get("userId"), userId),
                                    cb.notEqual(mr.get("status").get("lookupCode"), "DELETED"),
                                    cb.equal(mr.get("message").get("conversationId"), conversationId)
                            )
                    );

            // Main query selects messages whose IDs are in that subquery
            cq.select(messageRoot)
                    .where(
                            cb.in(messageRoot.get("id")).value(sub)
                    )
                    .orderBy(cb.asc(messageRoot.get("createdAt")));

            return session.createQuery(cq).getResultList();

        } catch (HibernateException e) {
            log.error("Error Message ; {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Message> findBySenderId(Long senderId) {

        try (Session session = sessionFactory.openSession()){
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Message> cq = cb.createQuery(Message.class);
            Root<Message> root = cq.from(Message.class);

            cq.select(root)
                    .where(cb.equal(root.get("sender").get("id"), senderId));

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Error Message ; {}",e.getMessage(),e);
            return null;
        }
    }
}
