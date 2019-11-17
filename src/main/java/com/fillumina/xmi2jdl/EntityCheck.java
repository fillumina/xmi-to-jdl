package com.fillumina.xmi2jdl;

import java.util.List;
import java.util.Map;

/**
 *
 * @author fra
 */
public class EntityCheck {
    private final Map<String, Entity> entities;
    private final Map<String, DataType> dataTypes;
    private final Map<String, Enumeration> enumerations;
    private final List<String> errors;

    public EntityCheck(Map<String, Entity> entities, 
            Map<String, DataType> dataTypes, 
            Map<String, Enumeration> enumerations, 
            List<String> errors) {
        this.entities = entities;
        this.dataTypes = dataTypes;
        this.enumerations = enumerations;
        this.errors = errors;
    }

    void check() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
