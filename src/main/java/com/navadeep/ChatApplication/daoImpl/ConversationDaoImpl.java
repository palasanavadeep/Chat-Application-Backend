package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.ConversationDao;
import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.UserLite;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ConversationDaoImpl implements ConversationDao {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Conversation save(Conversation conversation) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.save(conversation);
            tx.commit();
            return conversation;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Conversation update(Conversation conversation) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.update(conversation);
            tx.commit();
            return conversation;
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
            Conversation conversation = findById(id);
            if (conversation != null) {
                session.delete(conversation);
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
    public Conversation findById(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Conversation conversation = session.get(Conversation.class, id);
            tx.commit();
            return conversation;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Conversation> findAll() {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Conversation> query = session.createQuery("FROM Conversation", Conversation.class);
            List<Conversation> result = query.getResultList();
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
    public List<Conversation> findByUser(UserLite user) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Conversation> query = session.createQuery(
                    "SELECT c FROM Conversation c JOIN c.members m WHERE m.user.id = :userId",
                    Conversation.class);
            query.setParameter("userId", user.getId());
            List<Conversation> result = query.getResultList();
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
