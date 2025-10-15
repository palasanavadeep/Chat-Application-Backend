package com.navadeep.ChatApplication.dao;


import com.navadeep.ChatApplication.domain.Lookup;
import java.util.List;

public interface LookupDao {
    Lookup save(Lookup lookup);
    Lookup update(Lookup lookup);
    void delete(Long id);
    Lookup findById(Long id);
    List<Lookup> findAll();
    List<Lookup> findByCategory(String category);
}
