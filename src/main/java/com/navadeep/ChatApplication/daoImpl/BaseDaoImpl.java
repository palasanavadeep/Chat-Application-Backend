package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.BaseDao;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BaseDaoImpl<T> implements BaseDao<T> {


    protected SessionFactory sessionFactory;
    private final Class<T> tClass;

    Logger log =  LoggerFactory.getLogger(this.getClass());

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
            log.error("Error in findById({}) : {}",id,e.getMessage());
            return null;
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + tClass.getName(), tClass).list();
        }
        catch (HibernateException e) {
            log.error("Error in findAll() {}",e.getMessage());
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
            log.error("Error in save({}) : {}",entity,e.getMessage());
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
            log.error("Error in update({}) : {}",entity,e.getMessage());
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
            log.error("Error in delete({}) : {}",entity,e.getMessage());
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
