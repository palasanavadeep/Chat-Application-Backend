package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.ConversationParticipantDao;
import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.domain.UserLite;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ConversationParticipantDaoImpl implements ConversationParticipantDao {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ConversationParticipant save(ConversationParticipant participant) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.save(participant);
            tx.commit();
            return participant;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ConversationParticipant update(ConversationParticipant participant) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.update(participant);
            tx.commit();
            return participant;
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
            ConversationParticipant participant = findById(id);
            if (participant != null) {
                session.delete(participant);
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
    public ConversationParticipant findById(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            ConversationParticipant participant = session.get(ConversationParticipant.class, id);
            tx.commit();
            return participant;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ConversationParticipant> findAll() {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<ConversationParticipant> query = session.createQuery("FROM ConversationParticipant", ConversationParticipant.class);
            List<ConversationParticipant> result = query.getResultList();
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
    public List<ConversationParticipant> findByConversation(Conversation conversation) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<ConversationParticipant> query = session.createQuery(
                    "FROM ConversationParticipant WHERE conversation.id = :conversationId",
                    ConversationParticipant.class);
            query.setParameter("conversationId", conversation.getId());
            List<ConversationParticipant> result = query.getResultList();
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
    public ConversationParticipant findByConversationAndUser(Conversation conversation, UserLite user) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<ConversationParticipant> query = session.createQuery(
                    "FROM ConversationParticipant WHERE conversation.id = :conversationId AND user.id = :userId",
                    ConversationParticipant.class);
            query.setParameter("conversationId", conversation.getId());
            query.setParameter("userId", user.getId());
            ConversationParticipant result = query.uniqueResult();
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
