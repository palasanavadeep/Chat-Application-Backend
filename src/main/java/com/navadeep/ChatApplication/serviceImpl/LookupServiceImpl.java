package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.LookupDao;
import com.navadeep.ChatApplication.domain.Lookup;

import com.navadeep.ChatApplication.exception.BadRequestException;
import com.navadeep.ChatApplication.exception.NotFoundException;
import com.navadeep.ChatApplication.exception.ConflictException;
import com.navadeep.ChatApplication.exception.InternalServerException;

import com.navadeep.ChatApplication.service.LookupService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class LookupServiceImpl implements LookupService {

    private final LookupDao lookupDao;
    Log log = LogFactory.getLog(LookupServiceImpl.class);

    public LookupServiceImpl(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    @Override
    public Lookup save(String name, String category, String code) {

        if (category == null || code == null) {
            log.error("Category or code is NULL");
            throw new BadRequestException("Category or code cannot be NULL");
        }

        Lookup existing = lookupDao.findByCode(code);
        if (existing != null &&
                existing.getLookupCode().equals(code) &&
                existing.getLookupCategory().equals(category)) {
            log.error("Lookup code already exists");
            throw new ConflictException("Lookup code already exists");
        }

        Lookup lookup = new Lookup();
        lookup.setLookupCategory(category);
        lookup.setLookupCode(code);
        lookup.setLookupName(name);

        try {
            return lookupDao.save(lookup);
        } catch (Exception e) {
            log.error("Failed to save lookup: " + e.getMessage(), e);
            throw new InternalServerException("Failed to save lookup");
        }
    }

    @Override
    public Lookup update(Lookup lookup) {
        if (lookup == null || lookup.getId() == null) {
            log.error("Lookup or ID is null");
            throw new BadRequestException("Lookup or ID cannot be null");
        }

        if (lookup.getLookupCategory() == null || lookup.getLookupCode() == null) {
            log.error("Lookup category or code missing");
            throw new BadRequestException("Lookup category or code cannot be null");
        }

        try {
            return lookupDao.update(lookup);
        } catch (Exception e) {
            log.error("Failed to update lookup: " + e.getMessage(), e);
            throw new InternalServerException("Failed to update lookup");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            log.error("Lookup ID is null");
            throw new BadRequestException("ID cannot be null");
        }

        Lookup lookup = lookupDao.findById(id);
        if (lookup == null) {
            log.error("Lookup not found: " + id);
            throw new NotFoundException("Lookup not found for ID: " + id);
        }

        try {
            lookupDao.delete(lookup);
        } catch (Exception e) {
            log.error("Failed to delete lookup: " + e.getMessage(), e);
            throw new InternalServerException("Failed to delete lookup");
        }
    }

    @Override
    public Lookup findById(Long id) {
        if (id == null) {
            log.error("Lookup ID is null");
            throw new BadRequestException("ID cannot be null");
        }

        Lookup lookup = lookupDao.findById(id);
        if (lookup == null) {
            log.error("Lookup not found: " + id);
            throw new NotFoundException("Lookup not found for ID: " + id);
        }

        return lookup;
    }

    @Override
    public List<Lookup> findAll() {
        try {
            return lookupDao.findAll();
        } catch (Exception e) {
            log.error("Failed to fetch lookups: " + e.getMessage(), e);
            throw new InternalServerException("Failed to fetch lookups");
        }
    }

    @Override
    public List<Lookup> findByCategory(String category) {
        if (category == null || category.isEmpty()) {
            log.error("Category is empty");
            throw new BadRequestException("Category cannot be empty");
        }

        return lookupDao.findByCategory(category);
    }

    @Override
    public Lookup findByLookupCode(String lookupCode) {
        if (lookupCode == null || lookupCode.isEmpty()) {
            throw new BadRequestException("Lookup code cannot be empty");
        }

        Lookup lookup = lookupDao.findByCode(lookupCode.toUpperCase());
        if (lookup == null) {
            log.error("Lookup code not found: " + lookupCode);
            throw new NotFoundException("Lookup code not found: " + lookupCode);
        }

        return lookup;
    }
}
