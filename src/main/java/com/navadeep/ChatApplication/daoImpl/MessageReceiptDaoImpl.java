package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.MessageReceiptDao;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.domain.Message;
import com.navadeep.ChatApplication.domain.MessageReceipt;
import com.navadeep.ChatApplication.utils.Constants;
import jakarta.persistence.criteria.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class MessageReceiptDaoImpl extends BaseDaoImpl<MessageReceipt> implements MessageReceiptDao {

    public MessageReceiptDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory,MessageReceipt.class);
    }

    @Override
    public List<MessageReceipt> findByMessageId(Long messageId) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MessageReceipt> cq = cb.createQuery(MessageReceipt.class);
            Root<MessageReceipt> root = cq.from(MessageReceipt.class);

            cq.select(root)
                    .where(cb.equal(root.get("message").get("id"), messageId));

            return session.createQuery(cq).getResultList();

        }
        catch (HibernateException e) {
            log.error("findByMessageId("+messageId+") :: "+e.getMessage(),e);
            return null;
        }
    }

    @Override
    public void saveOrUpdateAll(List<MessageReceipt> receipts) {
        if (receipts == null || receipts.isEmpty()) return;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();
            int batchSize = Constants.BATCH_SIZE;
            for (int i = 0; i < receipts.size(); i++) {
                session.merge(receipts.get(i));
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            tx.commit();
        } catch (HibernateException e) {
            log.error("SaveOrUpdateAll() :: "+e.getMessage(),e);
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<MessageReceipt> findByUserIdAndMessageIds(Long userId, List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) return Collections.emptyList();

        try(Session session = sessionFactory.openSession()){

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MessageReceipt> cq = cb.createQuery(MessageReceipt.class);
            Root<MessageReceipt> root = cq.from(MessageReceipt.class);

//            Fetch<MessageReceipt, Message> messageFetch = root.fetch("message", JoinType.INNER);
//            Fetch<MessageReceipt, Lookup> statusFetch = root.fetch("status", JoinType.LEFT);

            cq.select(root)
                    .distinct(true)
                    .where(
                            cb.and(
                                    cb.equal(root.get("userId"), userId),
                                    root.get("message").get("id").in(messageIds)
                            )
                    );

            return session.createQuery(cq).getResultList();
        }
        catch (HibernateException e){
            log.error("findByUserIdAndMessageIds() :: "+e.getMessage(),e);
            return null;
        }
    }


    @Override
    public MessageReceipt findByUserIdAndMessageId(Long userId, Long messageId) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<MessageReceipt> cq = cb.createQuery(MessageReceipt.class);
            Root<MessageReceipt> root = cq.from(MessageReceipt.class);

            cq.select(root)
                    .where(
                            cb.and(
                                    cb.equal(root.get("userId"), userId),
                                    cb.equal(root.get("message").get("id"), messageId)
                            )
                    );

            return session.createQuery(cq).uniqueResultOptional().orElse(null);
        }
        catch (HibernateException e) {
            log.error("findByUserIdAndMessageId() :: "+e.getMessage(),e);
            return null;
        }
    }

    @Override
    public int markConversationAsRead(Long userId, Long conversationId, Lookup readStatus) {
        Transaction tx = null;
        int updatedCount = 0;

        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaUpdate<MessageReceipt> update = cb.createCriteriaUpdate(MessageReceipt.class);
            Root<MessageReceipt> root = update.from(MessageReceipt.class);

            update.set(root.get("status"), readStatus)
                    .where(
                            cb.and(
                                    cb.equal(root.get("userId"), userId),
                                    cb.equal(root.get("message").get("conversationId"), conversationId),
                                    cb.not(root.get("status").get("lookupCode").in("READ", "DELETED"))
                            )
                    );

            updatedCount = session.createMutationQuery(update).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            log.error("Error in marking Conversation("+conversationId+") As Read"+e.getMessage(),e);
            if (tx != null) tx.rollback();
        }

        return updatedCount;
    }

    @Override
    public int markMessageAsRead(Long userId, Long messageId, Lookup readStatus) {
        Transaction tx = null;
        int updatedCount = 0;

        try(Session session = sessionFactory.openSession())  {
            tx =  session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaUpdate<MessageReceipt> update = cb.createCriteriaUpdate(MessageReceipt.class);
            Root<MessageReceipt> root = update.from(MessageReceipt.class);

            update.set(root.get("status"), readStatus)
                    .where(
                            cb.and(
                                    cb.equal(root.get("userId"), userId),
                                    cb.equal(root.get("message").get("id"), messageId),
                                    cb.notEqual(root.get("status").get("lookupCode"), "DELETED")
                            )
                    );

            updatedCount = session.createMutationQuery(update).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            log.error("Error in marking the message("+messageId+") as Read "+e.getMessage(),e);
            if (tx != null) tx.rollback();
        }

        return updatedCount;
    }
}
