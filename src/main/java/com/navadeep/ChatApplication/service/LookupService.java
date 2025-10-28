package com.navadeep.ChatApplication.service;

import com.navadeep.ChatApplication.domain.ConversationParticipant;
import com.navadeep.ChatApplication.domain.Lookup;

import java.util.List;

public interface LookupService extends  BaseService<Lookup>{
    Lookup save(String name,String category,String code);
    Lookup update(Lookup lookup);
    void delete(Long id);
    List<Lookup> findByCategory(String category);
    Lookup findByLookupCode(String lookupCode);
}
