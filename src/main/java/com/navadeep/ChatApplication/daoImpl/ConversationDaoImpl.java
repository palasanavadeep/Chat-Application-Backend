package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.ConversationDao;
import com.navadeep.ChatApplication.domain.Conversation;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Collections;
import java.util.List;

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
                conversation.getConversationParticipants().add(participant);
                session.merge(conversation);
            }
            tx.commit();
        } catch (HibernateException e) {
            System.out.println("Error in Adding Participant : " + e.getMessage());
            log.error("Error in Adding Participant : {}",participant,e);
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
            participant.setLeftAt(System.currentTimeMillis());
            session.merge(participant);
            tx.commit();
        } catch (Exception e) {
            System.out.println("Error in removing participant : " + e.getMessage());
            log.error("Error in removing participant : {}",participantId,e);
            if (tx != null) tx.rollback();
            throw e;
        }
    }

//    check if userId's participant is not leftAt also
    @Override
    public List<Conversation> findUserConversations(Long userId) {
        try(Session session = sessionFactory.openSession()){
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Conversation> cq = cb.createQuery(Conversation.class);
            Root<Conversation> conversationRoot = cq.from(Conversation.class);

            // Join conversation -> participants
            Join<Conversation, ConversationParticipant> participantsJoin =
                    conversationRoot.join("conversationParticipants");

            cq.select(conversationRoot)
                    .distinct(true)
                    .where(
                            cb.and(
                                    cb.equal(participantsJoin.get("user").get("id"), userId),
                                    cb.isNull(participantsJoin.get("leftAt"))
                            )
                    );

            return session.createQuery(cq).getResultList();
        }
        catch (HibernateException e) {
            log.error("Error in findUserConversations :: ",e);
            return Collections.emptyList();
        }
    }


    // can optimize from conversationParticipants (Native SQL)
    @Override
    public List<ConversationParticipant> getAllParticipants(Long conversationId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ConversationParticipant> cq = cb.createQuery(ConversationParticipant.class);
            Root<ConversationParticipant> participantRoot = cq.from(ConversationParticipant.class);

            cq.select(participantRoot)
                    .where(
                            cb.and(
                                    cb.equal(participantRoot.get("conversationId"), conversationId),
                                    cb.isNull(participantRoot.get("leftAt"))
                            )
                    );

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Error in findAllParticipants :: ",e);
            return Collections.emptyList();
        }
    }

}