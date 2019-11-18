package com.fillumina.xmi2jdl;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class EntityDiagramValidator implements EntityDiagramConsumer {

    @Override
    public void consume(EntityDiagram validable) {
        new Validator(validable.getDataTypes(),
                validable.getEntities(),
                validable.getEnumerations());
    }

    private static class Validator {
        private final Map<String, DataType> dataTypes;
        private final Map<String, Entity> entities;
        private final Map<String, Enumeration> enumerations;

        public Validator(Map<String, DataType> dataTypes, 
                Map<String, Entity> entities, 
                Map<String, Enumeration> enumerations) {
            this.dataTypes = dataTypes;
            this.entities = entities;
            this.enumerations = enumerations;
            
            System.out.println("Executing Validator ...");
        }
        
    }
}
