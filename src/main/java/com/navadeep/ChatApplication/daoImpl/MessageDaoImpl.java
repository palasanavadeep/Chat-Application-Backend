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


    @Override
    public List<Message> findByConversationId(Long userId,Long conversationId) {
        try (Session session = sessionFactory.openSession()) {

//            CriteriaBuilder cb = session.getCriteriaBuilder();
//            CriteriaQuery<Message> cq = cb.createQuery(Message.class);
//            Root<Message> messageRoot = cq.from(Message.class);
//
//            // Join with MessageReceipt
//            Join<Message, MessageReceipt> receiptJoin = messageRoot.join("messageReceipts", JoinType.INNER);
//
//            cq.select(messageRoot)
//                    .distinct(true)
//                    .where(
//                            cb.and(
//                                    cb.equal(messageRoot.get("conversationId"), conversationId),
//                                    cb.equal(receiptJoin.get("userId"), userId),
//                                    cb.notEqual(receiptJoin.get("status").get("lookupCode"), "DELETED")
//                            )
//                    )
//                    .orderBy(cb.asc(messageRoot.get("createdAt")));
//
//            return session.createQuery(cq).getResultList();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Message> cq = cb.createQuery(Message.class);

// Define roots (similar to "from Message m join MessageReceipt mr on ...")
            Root<Message> m = cq.from(Message.class);
            Root<MessageReceipt> mr = cq.from(MessageReceipt.class);

// Define join condition: mr.message.id = m.id
            Predicate joinCondition = cb.equal(mr.get("message").get("id"), m.get("id"));

// Define filters
            Predicate conversationPredicate = cb.equal(m.get("conversationId"), conversationId);
            Predicate userPredicate = cb.equal(mr.get("userId"), userId);
            Predicate notDeletedPredicate = cb.notEqual(mr.get("status").get("lookupCode"), "DELETED");

// Combine all predicates
            cq.select(m)
                    .distinct(true)
                    .where(cb.and(joinCondition, conversationPredicate, userPredicate, notDeletedPredicate))
                    .orderBy(cb.asc(m.get("createdAt")));

            return session.createQuery(cq).getResultList();


        } catch (HibernateException e) {
            log.error("Error Message ; {}",e.getMessage(),e);
            return Collections.emptyList();
        }
    }



    // future versions
    @Override
    public List<Message> findBySenderId(Long senderId) {
        Session session = sessionFactory.getCurrentSession();
        try {
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
