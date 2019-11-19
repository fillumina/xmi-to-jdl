package com.fillumina.xmi2jdl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private int errorCounter;
    
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
        return entities.values().stream()
                .filter(e -> e.getName().equals(name)).findFirst();
    }
    
    protected List<Entity> findEntitiesByRegexp(String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        return entities.values().stream()
                .filter(e -> pattern.matcher(e.getName()).matches())
                .collect(Collectors.toList());
    }

    private void startTests() {
        System.out.println("Executing Validator");
    }

    private void endTests() {
        if (sameLine) ok();
        sameLine = false;
        System.out.println("");
        System.out.print("End Validator");
        if (errorCounter > 0) {
            System.out.print(", " + errorCounter + " ERRORS FOUND!");
        }
        System.out.println("");
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
    
    protected void warning(String... message) {
        if (sameLine) System.out.println("");
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        System.out.println("\tWARNING: " + msg);
    }
    
    protected void error(String... message) {
        error = true;
        errorCounter++;
        if (sameLine) System.out.println("");
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        System.out.println("\tERROR: " + msg);
    }
    
    public void allEntitisMustHaveADisplayField(String ... exempted) {
        test("allEntitisMustHaveADisplayField");
        
        List<String> exemptedList = Arrays.asList(exempted);
        
        entities.values().forEach( e -> {
            AtomicBoolean present = new AtomicBoolean(false);
            e.getDataTypes().forEach(r -> {
                present.set(present.get() || r.isDisplay());
            });
            if (!present.get() && !exemptedList.contains(e.getName())) {
                warning("Entity", e.getName(), "does not have display");
            }
        });
        
        log("Exceptions: " + 
                exemptedList.stream().collect(Collectors.joining(", ")));
        
        endTest();
    }
    
    // TODO warning for lone Entity or linked by only one
}
