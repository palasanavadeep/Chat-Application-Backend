package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.BaseDao;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class BaseDaoImpl<T> implements BaseDao<T> {

    protected SessionFactory sessionFactory;
    private Class<T> tClass;

    public BaseDaoImpl(SessionFactory sessionFactory, Class<T> tClass) {
        this.sessionFactory = sessionFactory;
        this.tClass = tClass;
    }

    @Override
    public T findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(tClass, id);
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + tClass.getName(), tClass).list();
        }
        catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T save(T entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (HibernateException e) {
            e.printStackTrace();
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public T update(T entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
            return entity;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public void delete(T entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
