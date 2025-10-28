package com.navadeep.ChatApplication.dao;


import com.navadeep.ChatApplication.domain.Lookup;
import java.util.List;

public interface LookupDao extends BaseDao<Lookup>{

    List<Lookup> findByCategory(String category);
    Lookup findByCode(String code);
}
