package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.UserDao;
import com.navadeep.ChatApplication.domain.User;
import com.navadeep.ChatApplication.domain.UserLite;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {

    public UserDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
}