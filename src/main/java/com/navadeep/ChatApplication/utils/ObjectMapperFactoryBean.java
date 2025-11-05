package com.navadeep.ChatApplication.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.FactoryBean;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.Module;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {
    private List<Module> modules = new ArrayList<>();

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public ObjectMapper getObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        for (Module module : modules) {
            mapper.registerModule(module);
        }
        return mapper;
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }
}
