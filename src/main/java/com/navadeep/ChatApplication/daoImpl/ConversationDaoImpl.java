package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.ConversationDao;
import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationDaoImpl extends BaseDaoImpl<Conversation> implements ConversationDao {

    public ConversationDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Conversation.class);
    }


    @Override
    public void addParticipant(Long conversationId, ConversationParticipant participant) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Conversation conversation = session.get(Conversation.class, conversationId);
            if (conversation != null) {
                participant.setLeftAt(null);
                conversation.getConversationParticipants().add(participant);
                session.merge(conversation);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void removeParticipant(Long conversationId, Long participantId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ConversationParticipant participant = session.get(ConversationParticipant.class, participantId);
            participant.setLeftAt(LocalDateTime.now());
            session.merge(participant);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<Conversation> findUserConversations(Long userId) {
        try(Session session = sessionFactory.openSession()){
            String hql = """
            select distinct c
            from Conversation c
            join c.conversationParticipants cpUser
            join fetch c.conversationParticipants cpAll
            left join fetch c.lastMessage lm
            where cpUser.user.id = :userId
              and cpUser.leftAt is null
            order by lm.createdAt desc
        """;

            return session.createQuery(hql, Conversation.class)
                    .setParameter("userId", userId)
                    .list();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }


    // can optimize from conversationParticipants (Native SQL)
    @Override
    public List<ConversationParticipant> getAllParticipants(Long conversationId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
            select cp
            from ConversationParticipant cp
            join fetch cp.user
            where cp.conversation.id = :conversationId
              and cp.leftAt is null
        """;
            return session.createQuery(hql, ConversationParticipant.class)
                    .setParameter("conversationId", conversationId)
                    .list();
        } catch (HibernateException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



}


//    @Override
//    public List<Conversation> findByUserId(Long userId) {
//        Session session = sessionFactory.getCurrentSession();
//
//        try {
//            String hql = """
//            SELECT DISTINCT c
//            FROM Conversation c
//            JOIN c.members m
//            WHERE m.user.id = :userId
//            AND c.id NOT IN (
//                SELECT ev.message.conversation.id
//                FROM UserEvent ev
//                WHERE ev.user.id = :userId
//            )
//            """;
//
//            Query<Conversation> query = session.createQuery(hql, Conversation.class);
//            query.setParameter("userId", user.getId());
//
//            return query.getResultList();
//        } catch (HibernateException e) {
//            e.printStackTrace();
//            return List.of();
//        }
//    }
