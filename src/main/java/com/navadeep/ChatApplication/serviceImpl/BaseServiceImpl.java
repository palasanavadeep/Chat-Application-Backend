package com.navadeep.ChatApplication.serviceImpl;

import com.navadeep.ChatApplication.dao.BaseDao;
import com.navadeep.ChatApplication.service.BaseService;

import java.util.List;

public class BaseServiceImpl<T> implements BaseService<T> {

    protected final BaseDao<T> dao;
    protected BaseServiceImpl(BaseDao<T> dao) {
        this.dao = dao;
    }


    @Override
    public T findById(Long id) {
        if(id != null){
            return dao.findById(id);
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        return dao.findAll();
    }
}
