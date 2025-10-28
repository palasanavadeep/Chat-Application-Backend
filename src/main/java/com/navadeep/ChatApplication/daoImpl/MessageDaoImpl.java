package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.MessageDao;
import com.navadeep.ChatApplication.domain.Message;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class MessageDaoImpl extends BaseDaoImpl<Message> implements MessageDao {

    public MessageDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Message.class);
    }

//    @Override
//    public List<Message> findByConversationId(Long conversationId) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("from Message where conversationId = :cid order by createdAt", Message.class)
//                    .setParameter("cid", conversationId)
//                    .list();
//        }
//        catch (HibernateException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public List<Message> findByConversationId(Long userId,Long conversationId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
            select m
            from Message m
            join MessageReceipt mr on mr.message.id = m.id
            where m.conversationId = :cid
              and mr.userId = :uid
              and mr.status.lookupCode != 'DELETED'
            order by m.createdAt
        """;

            return session.createQuery(hql, Message.class)
                    .setParameter("cid", conversationId)
                    .setParameter("uid", userId)
                    .list();
        } catch (HibernateException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    // future versions
    @Override
    public List<Message> findBySenderId(Long senderId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE sender.id = :senderId",
                    Message.class);
            query.setParameter("senderId", senderId);
            List<Message> result = query.getResultList();
            return result;
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
}
