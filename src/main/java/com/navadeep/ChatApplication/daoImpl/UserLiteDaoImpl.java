package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.UserLiteDao;
import com.navadeep.ChatApplication.domain.UserLite;
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
            return session.createQuery("from UserLite where username = :uname", UserLite.class)
                    .setParameter("uname", username)
                    .uniqueResult();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
}
