package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.BaseDao;
import com.navadeep.ChatApplication.exception.BadRequestException;
import com.navadeep.ChatApplication.exception.NotFoundException;
import com.navadeep.ChatApplication.service.BaseService;

import java.util.List;

public class BaseServiceImpl<T> implements BaseService<T> {

    protected final BaseDao<T> dao;

    protected BaseServiceImpl(BaseDao<T> dao) {
        this.dao = dao;
    }

    @Override
    public T findById(Long id) {
        if (id == null) {
            throw new BadRequestException("ID cannot be null");
        }
        T entity = dao.findById(id);
        if (entity == null) {
            throw new NotFoundException("Entity not found for ID: " + id);
        }

        return entity;
    }

    @Override
    public List<T> findAll() {
        List<T> entities = dao.findAll();
        if (entities == null || entities.isEmpty()) {
            throw new NotFoundException("No entities found");
        }
        return entities;
    }
}
