package com.fillumina.xmi2jdl;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author fra
 */
public class EntityDiagramValidator implements EntityDiagramConsumer {

    @Override
    public void consume(EntityDiagram validable) {
        new Validator(validable.getDataTypes(),
                validable.getEntities(),
                validable.getEnumerations()).test();
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
        }
        
        void test() {
            System.out.println("Executing Validator ...");
            this.allConnectedToDiscountMustHaveNameField();
            System.out.println("End Validator");
        }
        
        void allConnectedToDiscountMustHaveNameField() {
            System.out.println("testing allConnectedToDiscountMustHaveNameField...");
            
            findByName("Detail").ifPresentOrElse(( Entity detail) -> {
                detail.getAllRelationships().stream()
                        .map(e -> e.getOwner())
                        .filter(e -> e != detail && e.getFieldByName("name").isEmpty())
                        .forEach(e -> error("Entity missing 'name': ", e.getName()));
                
            }, () -> error("Detail not found!"));
        }
        
        private Optional<Entity> findByName(String name) {
            return entities.values().stream()
                    .filter(e -> e.getName().equals(name))
                    .findFirst();
        }
        
        private void error(String ...message) {
            String msg = Arrays.stream(message).collect(Collectors.joining(" "));
            System.out.println("ERROR: " + msg);
        }
    }
}
