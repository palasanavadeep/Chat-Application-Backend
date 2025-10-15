package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.MessageDao;
import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.Message;
import com.navadeep.ChatApplication.domain.UserLite;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MessageDaoImpl implements MessageDao {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Message save(Message message) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            if (message.getMessageAttachment() != null) {
                session.save(message.getMessageAttachment());
            }
            session.save(message);
            tx.commit();
            return message;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Message update(Message message) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            if (message.getMessageAttachment() != null) {
                session.update(message.getMessageAttachment());
            }
            session.update(message);
            tx.commit();
            return message;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Message message = findById(id);
            if (message != null) {
                if (message.getMessageAttachment() != null) {
                    session.delete(message.getMessageAttachment());
                }
                session.delete(message);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Message findById(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Message message = session.get(Message.class, id);
            tx.commit();
            return message;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Message> findAll() {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Message> query = session.createQuery("FROM Message", Message.class);
            List<Message> result = query.getResultList();
            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Message> findByConversation(Conversation conversation) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE conversation.id = :conversationId",
                    Message.class);
            query.setParameter("conversationId", conversation.getId());
            List<Message> result = query.getResultList();
            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Message> findBySender(UserLite sender) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE sender.id = :senderId",
                    Message.class);
            query.setParameter("senderId", sender.getId());
            List<Message> result = query.getResultList();
            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
}
