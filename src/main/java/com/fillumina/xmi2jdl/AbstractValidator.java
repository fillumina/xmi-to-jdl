package com.fillumina.xmi2jdl;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author fra
 */
public abstract class AbstractValidator implements EntityDiagramConsumer {
    protected Map<String, DataType> dataTypes;
    protected Map<String, Entity> entities;
    protected Map<String, Enumeration> enumerations;

    private boolean testing;
    
    @Override
    public void consume(EntityDiagram entityDiagram) {
        dataTypes = entityDiagram.getDataTypes();
        entities = entityDiagram.getEntities();
        enumerations = entityDiagram.getEnumerations();
        executeTests();
    }

    abstract void executeTests();
    
    protected Optional<Entity> findEntityByName(String name) {
        return entities.values().stream().filter((e) -> e.getName().equals(name)).findFirst();
    }

    protected void startTests() {
        System.out.println("Executing Validator");
    }

    protected void endTests() {
        if (testing) ok();
        testing = false;
        System.out.println("");
        System.out.println("End Validator");
    }
    
    protected void test(String name) {
        if (testing) ok();
        System.out.println("");
        System.out.print("testing " + name + " ...");
        testing = true;
    }
    
    protected void ok() {
        System.out.println(" OK!");
        testing = false;
    }
    
    protected void error(String... message) {
        testing = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        System.out.println("");
        System.out.println("ERROR: " + msg);
    }
    
}
