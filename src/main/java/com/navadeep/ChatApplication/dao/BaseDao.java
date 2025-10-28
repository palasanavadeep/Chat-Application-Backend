package com.navadeep.ChatApplication.dao;

import java.util.List;

public interface BaseDao<T> {
    T findById(Long id);
    List<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(T entity);
}
