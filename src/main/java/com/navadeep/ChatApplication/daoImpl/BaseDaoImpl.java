package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.BaseDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


import java.util.List;

public class BaseDaoImpl<T> implements BaseDao<T> {


    protected SessionFactory sessionFactory;
    private final Class<T> tClass;

    Log log =  LogFactory.getLog(this.getClass());

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
            log.error("Error in findById("+id+") : "+e.getMessage(),e);
            throw e;
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();

            CriteriaQuery<T> cq = cb.createQuery(tClass);

            Root<T> root = cq.from(tClass);

            cq.select(root);

            return session.createQuery(cq).getResultList();
        }
        catch (HibernateException e) {
            log.error("Error in findAll() :"+e.getMessage(),e);
            throw e;
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
            log.error("Error in save("+entity+") : "+e.getMessage(),e);
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
            log.error("Error in update("+entity+") : "+e.getMessage(),e);
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
            log.error("Error in delete("+entity+") : "+e.getMessage(),e);
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
