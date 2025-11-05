package com.navadeep.ChatApplication.daoImpl;

import com.navadeep.ChatApplication.dao.LookupDao;
import com.navadeep.ChatApplication.domain.Lookup;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class LookupDaoImpl extends BaseDaoImpl<Lookup> implements LookupDao {

    public LookupDaoImpl(SessionFactory sessionFactory){
        super(sessionFactory,Lookup.class);
    }

    @Override
    public List<Lookup> findByCategory(String category) {
        try (Session session = sessionFactory.openSession()){

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Lookup> cq = cb.createQuery(Lookup.class);
            Root<Lookup> lookupRoot = cq.from(Lookup.class);

            cq.select(lookupRoot)
                    .where(cb.equal(lookupRoot.get("lookupCategory"), category));

            return session.createQuery(cq).getResultList();
        } catch (HibernateException e) {
            log.error("Error Message :: {}",e.getMessage(),e);
            return null;
        }
    }

    @Override
    public Lookup findByCode(String code) {
        try (Session session = sessionFactory.openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Lookup> cq = cb.createQuery(Lookup.class);
            Root<Lookup> lookupRoot = cq.from(Lookup.class);

            cq.select(lookupRoot)
                    .where(cb.equal(lookupRoot.get("lookupCode"), code));

            return session.createQuery(cq).uniqueResultOptional().orElse(null);
        }
        catch (HibernateException e) {
            log.error("Error Message :: {}",e.getMessage(),e);
            return null;
        }
    }
}