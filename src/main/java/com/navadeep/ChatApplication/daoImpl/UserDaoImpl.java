package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.domain.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

    public UserDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> userRoot = cq.from(User.class);

            cq.select(userRoot)
                    .where(
                            cb.equal(userRoot.get("email"), email)
                    );

            return session.createQuery(cq).uniqueResultOptional().orElse(null);
        }
        catch (HibernateException e) {
            log.error("Error Message : {}",e.getMessage(),e);
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> userRoot = cq.from(User.class);

            cq.select(userRoot)
                    .where(
                            cb.equal(userRoot.get("username"), username)
                    );

            return session.createQuery(cq).uniqueResultOptional().orElse(null);
        }
        catch (HibernateException e) {
            log.error("Error Message : {}",e.getMessage(),e);
            return null;
        }
    }
}