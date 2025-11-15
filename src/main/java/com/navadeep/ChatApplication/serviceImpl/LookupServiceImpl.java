package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.LookupDao;
import com.navadeep.ChatApplication.daoImpl.LookupDaoImpl;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.service.LookupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class LookupServiceImpl implements LookupService {

    private final LookupDao lookupDao;
    Log log = LogFactory.getLog(LookupServiceImpl.class);
    public LookupServiceImpl(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    @Override
    public Lookup save(String name,String category, String code) {
        if(category == null || code == null || category.isEmpty() || code.isEmpty()){
            log.error("Category or code is null or code is empty");
            throw new RuntimeException("Category or code is null or empty");
        }

        Lookup checkCode = lookupDao.findByCode(code);
        if(checkCode != null
                && checkCode.getLookupCode().equals(code)
                && checkCode.getLookupCategory().equals(category)){
            log.error("Code is already exist");
            throw new RuntimeException("Code is already exist");
        }

        Lookup newLookup = new Lookup();
        newLookup.setLookupCategory(category);
        newLookup.setLookupCode(code);
        newLookup.setLookupName(name);

        return lookupDao.save(newLookup);

    }

    @Override
    public Lookup update(Lookup lookup) {
        if(lookup == null || lookup.getId() == null){
            log.error("Lookup id is null or lookupId is null");
            throw new RuntimeException("Lookup id is null or empty");
        }

        if(lookup.getLookupCategory() == null || lookup.getLookupCode() == null){
            log.error("Lookup category or code is null or empty");
            throw new RuntimeException("Lookup code is null or empty");
        }

        return lookupDao.update(lookup);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            log.error("Lookup id is null");
            throw new RuntimeException("Lookup id is null or empty");
        }
        Lookup lookup = lookupDao.findById(id);
        if(lookup == null){
            log.error("Lookup with ID :  "+id+" not found");
            throw new RuntimeException("Lookup with ID :  "+id+" not found");
        }
        lookupDao.delete(lookup);
    }

    @Override
    public Lookup findById(Long id) {
        if (id == null) {
            log.error("Lookup id is null");
            throw new RuntimeException("Lookup id is null ");
        }
        Lookup lookup = lookupDao.findById(id);
        if(lookup == null){
            log.error("Lookup with ID :  "+id+" not found");
            throw new RuntimeException("Lookup with ID :  "+id+" not found");
        }
        return lookup;
    }

    @Override
    public List<Lookup> findAll() {
        return  lookupDao.findAll();
    }

    @Override
    public List<Lookup> findByCategory(String category) {
        if(category == null || category.isEmpty()){
            log.error("Category is null or empty");
            throw new RuntimeException("Category is null or empty");
        }
        return lookupDao.findByCategory(category);
    }

    @Override
    public Lookup findByLookupCode(String lookupCode) {
        Lookup lookup = lookupDao.findByCode(lookupCode.toUpperCase());
        if(lookup == null){
            throw new RuntimeException("Lookup code :  "+lookupCode+" not found");
        }
        return lookup;
    }
}