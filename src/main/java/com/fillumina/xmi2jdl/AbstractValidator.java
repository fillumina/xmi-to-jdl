package com.fillumina.xmi2jdl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private int warningCounter;
    private boolean verbose;
    
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

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
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
        System.out.println("/*");
        System.out.println("Executing Validator");
    }

    private void endTests() {
        if (sameLine) ok();
        sameLine = false;
        System.out.println("");
        System.out.print("End Validator");
        if (warningCounter > 0) {
            System.out.print(", " + warningCounter + " WARNINGS FOUND");
        }
        if (errorCounter > 0) {
            System.out.print(", " + errorCounter + " ERRORS FOUND");
        }
        System.out.println("");
        System.out.println("*/");
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
        if (!verbose) return;
        if (sameLine) System.out.println("");
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        System.out.println(" . " + msg);
    }
    
    protected void warning(String... message) {
        if (sameLine) System.out.println("");
        sameLine = false;
        warningCounter++;
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
    

    void forbiddenEntityNameCheck(String ... names) {
        test("forbiddenEntityNameCheck");

        Arrays.asList(names).forEach(n -> {
            findEntitiesByRegexp(n).stream().forEach(e -> {
                error("Bad name for entity '", e.getName() , "'");
            });
        });
        
        endTest();
    }
    
    public void allEntitisMustHaveADisplayFieldExcept(String ... exempted) {
        test("all entities must have a display field");
        
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
    
    void allConnectedMustHaveNameFields(String entityName, String... fieldNames) {
        test("all connected to " + entityName + 
                " must have field named " + Arrays.toString(fieldNames));

        findEntityByName(entityName).ifPresentOrElse(( Entity detail) -> {
            detail.getAllRelationships().stream()
                    .map(e -> e.getOwner())
                    .filter(e -> e != detail)
                    .peek(e -> log("checking " + e.getName())) 
                    .filter(e -> Arrays.stream(fieldNames)
                            .allMatch(f -> e.getFieldByName(f).isEmpty()))
                    .forEach(e -> error("Entity missing ", e.getName()));

        }, () -> error(entityName + " not found!"));
        
        endTest();
    }
        
    void allConnectedEntitiesMustHaveRelationNamedTheSame() {
        
        entities.values().forEach(e -> {
            String entityName = e.getName();
            String fieldName = "" + Character.toLowerCase(entityName.charAt(0)) +
                    entityName.substring(1);
            allConnectedMustHaveRelationName(entityName, fieldName);
        });
    }
    
    void allConnectedMustHaveRelationName(String entityName, String fieldName) {
        test("all connected to " + entityName + 
                " must have the relation named " + fieldName);

        findEntityByName(entityName).ifPresentOrElse(( Entity entity) -> {
            entity.getAllRelationships().stream()
                    .filter(r -> !r.getRelationshipType().equals(RelationshipType.OneToMany))
                    .map(r -> r.getOwner())
                    .filter(e -> e != entity)
                    .peek(e -> log("checking " + e.getName())) 
                    .filter(e -> e.getRelationByName(fieldName).isEmpty())
                    .forEach(e -> error("Entity missing '" + fieldName + 
                            "': ", e.getName()));

        }, () -> error(entityName + " not found!"));
        
        endTest();
    }
    
    void allConnectedMustHaveUnidirectionalRelationExcept(String entityName, 
            String ... exceptions) {
        test("all connected to " + entityName + 
                " must have unidirectional relationship except: " + 
                Arrays.toString(exceptions));

        List<String> ex = Arrays.asList(exceptions);
        
        findEntityByName(entityName).ifPresentOrElse(( Entity entity) -> {
            entity.getAllRelationships().stream()
                    .filter(r -> !ex.contains(r.getOwner().getName()) )
                    .filter(r -> r.getOwner() != entity)
                    .filter(r -> !r.getRelationshipType().equals(RelationshipType.OneToMany))
                    .peek(r -> log("checking " + r.getOwner().getName() + " ..."))
                    .filter(r -> !r.isUnidirectional())
                    .forEach(r -> error("not unidirectional from " + 
                            r.getOwner().getName()));

        }, () -> error(entityName + " not found!"));
        
        endTest();
    }
    
    void noCircularOneToOneWithMapIdRelationships() {
        test("test circular 1 to 1 with @MapId");
        
        entities.values().forEach(e -> {
            Set<Entity> equalIdSet = new HashSet<>();
            String entityName = e.getName();
            e.getMapIdConnectedEntityList().forEach(c -> {
                if (equalIdSet.contains(c)) {
                    error("circular @MapId with ", e.getName(), " and ", c.getName());
                }
                equalIdSet.add(c);
            });
        });
    }
    
    // TODO warning for lone Entity or linked by only one
}
