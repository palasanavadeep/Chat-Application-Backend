package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.LookupDao;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.domain.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class LookupDaoImpl extends BaseDaoImpl<Lookup> implements LookupDao {

    public LookupDaoImpl(SessionFactory sessionFactory){
        super(sessionFactory,Lookup.class);
    }

    @Override
    public List<Lookup> findByCategory(String category) {
        try (Session session = sessionFactory.openSession()){
            Query<Lookup> query = session.createQuery("FROM Lookup WHERE lookupCategory = :category", Lookup.class);
            query.setParameter("category", category);
            List<Lookup> result = query.getResultList();
            return result;
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Lookup findByCode(String code) {
        System.out.println("code:"+code);
        try (Session session = sessionFactory.openSession()) {

            return session.createQuery("from Lookup where lookupCode = :code", Lookup.class)
                    .setParameter("code", code)
                    .uniqueResult();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
}