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

import java.util.Collections;
import java.util.List;

public class ConversationParticipantDaoImpl extends BaseDaoImpl<ConversationParticipant> implements ConversationParticipantDao {

    public ConversationParticipantDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, ConversationParticipant.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Long> findParticipantUserIdsByConversationId(Long conversationId) {
        String sql = "SELECT user_id FROM conversation_participants " +
                "WHERE conversation_id = :conversationId AND left_at IS NULL";

        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(sql)
                    .setParameter("conversationId", conversationId)
                    .list();
        } catch (HibernateException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    @Override
    public ConversationParticipant getParticipantByConversationIdAndUserId(Long conversationId, Long userId) {
        try(Session session = sessionFactory.openSession()){

            String sql = "SELECT * FROM conversation_participant WHERE user_id = :userId AND conversation_id = :conversationId";
            ConversationParticipant participant = session
                    .createNativeQuery(sql, ConversationParticipant.class)
                    .setParameter("userId", userId)
                    .setParameter("conversationId", conversationId)
                    .uniqueResult();

            return participant;

        }
        catch(HibernateException ex){
            ex.printStackTrace();
            return null;
        }
    }

}

//@Override
//public List<ConversationParticipant> findByConversation(Conversation conversation) {
//    Session session = sessionFactory.getCurrentSession();
//    try {
//        Query<ConversationParticipant> query = session.createQuery(
//                "FROM ConversationParticipant WHERE conversation.id = :conversationId",
//                ConversationParticipant.class);
//        query.setParameter("conversationId", conversation.getId());
//        List<ConversationParticipant> result = query.getResultList();
//        return result;
//    } catch (HibernateException e) {
//        e.printStackTrace();
//        return null;
//    }
//}
//
//@Override
//public ConversationParticipant findByConversationAndUser(Conversation conversation, UserLite user) {
//    Session session = sessionFactory.getCurrentSession();
//    try {
//        Query<ConversationParticipant> query = session.createQuery(
//                "FROM ConversationParticipant WHERE conversation.id = :conversationId AND user.id = :userId",
//                ConversationParticipant.class);
//        query.setParameter("conversationId", conversation.getId());
//        query.setParameter("userId", user.getId());
//        ConversationParticipant result = query.uniqueResult();
//        return result;
//    } catch (HibernateException e) {
//        e.printStackTrace();
//        return null;
//    }
//}
