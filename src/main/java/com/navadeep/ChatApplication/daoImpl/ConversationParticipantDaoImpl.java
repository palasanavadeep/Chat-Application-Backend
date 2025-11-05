package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.ConversationParticipantDao;
import com.navadeep.ChatApplication.domain.ConversationParticipant;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.Collections;
import java.util.List;

public class ConversationParticipantDaoImpl extends BaseDaoImpl<ConversationParticipant> implements ConversationParticipantDao {

    public ConversationParticipantDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, ConversationParticipant.class);
    }

    @Override
    public List<Long> findParticipantUserIdsByConversationId(Long conversationId) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<ConversationParticipant> participantRoot = cq.from(ConversationParticipant.class);

            cq.select(participantRoot.get("user").get("id"))
                    .where(
                            cb.and(
                                    cb.equal(participantRoot.get("conversationId"), conversationId),
                                    cb.isNull(participantRoot.get("leftAt"))
                            )
                    );

            return session.createQuery(cq).getResultList();

        } catch (HibernateException e) {
            log.error("Error Message :: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    @Override
    public ConversationParticipant getParticipantByConversationIdAndUserId(Long conversationId, Long userId) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ConversationParticipant> cq = cb.createQuery(ConversationParticipant.class);
            Root<ConversationParticipant> participantRoot = cq.from(ConversationParticipant.class);

            cq.select(participantRoot)
                    .where(
                            cb.and(
                                    cb.equal(participantRoot.get("conversationId"), conversationId),
                                    cb.equal(participantRoot.get("user").get("id"), userId)
                            )
                    );

            return session.createQuery(cq).uniqueResultOptional().orElse(null);

        } catch (HibernateException e) {
            log.error("Error Message : {}", e.getMessage(), e);
            return null;
        }
    }

}