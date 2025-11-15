package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.domain.UserLite;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserLiteDaoImpl extends BaseDaoImpl<UserLite> implements UserLiteDao {

    public UserLiteDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, UserLite.class);
    }

    @Override
    public UserLite findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<UserLite> cq = cb.createQuery(UserLite.class);
            Root<UserLite> userLiteRoot = cq.from(UserLite.class);

            cq.select(userLiteRoot)
                    .where(
                            cb.equal(userLiteRoot.get("username"), username)
                    );

            return session.createQuery(cq).uniqueResultOptional().orElse(null);
        }
        catch (HibernateException e) {
            log.error("findByUsername("+username+") :: "+e.getMessage(),e);
            throw e;
        }
    }
}
