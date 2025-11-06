package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.LookupDao;
import com.navadeep.ChatApplication.daoImpl.LookupDaoImpl;
import com.navadeep.ChatApplication.domain.Lookup;
import com.navadeep.ChatApplication.service.LookupService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class LookupServiceImpl implements LookupService {

    private final LookupDao lookupDao;

    public LookupServiceImpl(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    @Override
    public Lookup save(String name,String category, String code) {
        if(category == null || code == null || category.isEmpty() || code.isEmpty()){
            throw new RuntimeException("Category or code is null or empty");
        }

        Lookup checkCode = lookupDao.findByCode(code);
        if(checkCode != null
                && checkCode.getLookupCode().equals(code)
                && checkCode.getLookupCategory().equals(category)){
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
            throw new RuntimeException("Lookup id is null or empty");
        }

        if(lookup.getLookupCategory() == null || lookup.getLookupCode() == null){
            throw new RuntimeException("Lookup code is null or empty");
        }

        return lookupDao.update(lookup);
    }

    @Override
    public void delete(Long id) {
        Lookup lookup = lookupDao.findById(id);
        if(lookup == null){
            throw new RuntimeException("Lookup with ID :  "+id+" not found");
        }
        lookupDao.delete(lookup);
    }

    @Override
    public Lookup findById(Long id) {
        Lookup lookup = lookupDao.findById(id);
        if(lookup == null){
            throw new RuntimeException("Lookup with ID :  "+id+" not found");
        }
        return lookup;
    }

    @Override
    public List<Lookup> findAll() {
        List<Lookup> lookups = lookupDao.findAll();
        if(lookups == null){
            throw new RuntimeException("Lookups is null or empty");
        }
        return lookups;
    }

    @Override
    public List<Lookup> findByCategory(String category) {
        List<Lookup> lookups = lookupDao.findByCategory(category);
        if(lookups == null){
            throw new RuntimeException("Lookups for Category :  "+category+" not found");
        }
        return lookups;
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