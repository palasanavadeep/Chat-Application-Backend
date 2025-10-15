package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.LookupDao;
import com.navadeep.ChatApplication.domain.Lookup;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class LookupDaoImpl implements LookupDao {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Lookup save(Lookup lookup) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.save(lookup);
            tx.commit();
            return lookup;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Lookup update(Lookup lookup) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            session.update(lookup);
            tx.commit();
            return lookup;
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
            Lookup lookup = findById(id);
            if (lookup != null) {
                session.delete(lookup);
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
    public Lookup findById(Long id) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Lookup lookup = session.get(Lookup.class, id);
            tx.commit();
            return lookup;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Lookup> findAll() {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Lookup> query = session.createQuery("FROM Lookup", Lookup.class);
            List<Lookup> result = query.getResultList();
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
    public List<Lookup> findByCategory(String category) {
        Transaction tx = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            tx = session.beginTransaction();
            Query<Lookup> query = session.createQuery("FROM Lookup WHERE lookupCategory = :category", Lookup.class);
            query.setParameter("category", category);
            List<Lookup> result = query.getResultList();
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