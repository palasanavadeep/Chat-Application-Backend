package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.MessageReceiptDao;
import com.navadeep.ChatApplication.domain.MessageReceipt;
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
            return session.createQuery("from MessageReceipt mr where = :mid", MessageReceipt.class)
                    .setParameter("mid", messageId)
                    .list();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveOrUpdateAll(List<MessageReceipt> receipts) {
        if (receipts == null || receipts.isEmpty()) return;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();
            int batchSize = 50;
            for (int i = 0; i < receipts.size(); i++) {
                session.merge(receipts.get(i));
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Object[]> findByUserIdAndMessageIds(Long userId, List<Long> lastMessageIds) {
        if (lastMessageIds == null || lastMessageIds.isEmpty()) return Collections.emptyList();

        try(Session session = sessionFactory.openSession()){
            String hql = """
            select mr.message.id, mr.status.lookupCode
            from MessageReceipt mr
            where mr.userId = :userId
              and mr.message.id in (:lastMessageIds)
        """;

            return session.createQuery(hql, Object[].class)
                    .setParameter("userId", userId)
                    .setParameterList("lastMessageIds", lastMessageIds)
                    .list();
        }
        catch (HibernateException e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public MessageReceipt findByUserIdAndMessageId(Long userId, Long messageId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from MessageReceipt mr where mr.message.id = :mId and userId = :uId", MessageReceipt.class)
                    .setParameter("mId", messageId)
                    .setParameter("uId", userId)
                    .uniqueResult();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
}
