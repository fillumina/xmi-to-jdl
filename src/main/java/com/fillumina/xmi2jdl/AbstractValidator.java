package com.fillumina.xmi2jdl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author fra
 */
public abstract class AbstractValidator implements EntityDiagramConsumer {
    protected Map<String, DataType> dataTypes;
    protected Map<String, Entity> entities;
    protected Map<String, Enumeration> enumerations;

    private boolean sameLine;
    private boolean error;
    
    @Override
    public void consume(EntityDiagram entityDiagram) {
        dataTypes = entityDiagram.getDataTypes();
        entities = entityDiagram.getEntities();
        enumerations = entityDiagram.getEnumerations();
        
        startTests();
        executeTests();
        endTests();
    }

    abstract void executeTests();
    
    protected Optional<Entity> findEntityByName(String name) {
        return entities.values().stream().filter((e) -> e.getName().equals(name)).findFirst();
    }
    
    protected List<Entity> findEntitiesByName(String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        return entities.values().stream()
                .filter(e -> pattern.matcher(e.getName()).matches())
                .collect(Collectors.toList());
    }

    protected void startTests() {
        System.out.println("Executing Validator");
    }

    protected void endTests() {
        if (sameLine) ok();
        sameLine = false;
        System.out.println("");
        System.out.println("End Validator");
    }
    
    protected void test(String name) {
        if (sameLine) ok();
        System.out.println("");
        System.out.print("testing " + name + " ...");
        sameLine = true;
        error = false;
    }
    
    protected void endTest() {
        if (error) {
            System.out.println("ERROR!");
        } else {
            ok();
        }
    }
        
    protected void ok() {
        System.out.println(" OK!");
        sameLine = false;
    }
    
    protected void log(String... message) {
        if (sameLine) System.out.println("");
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        System.out.println(" . " + msg);
    }
    
    protected void error(String... message) {
        error = true;
        if (sameLine) System.out.println("");
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        System.out.println("ERROR: " + msg);
    }
    
}
