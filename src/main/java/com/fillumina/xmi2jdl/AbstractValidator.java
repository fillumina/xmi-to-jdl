package com.fillumina.xmi2jdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        
    protected List<Entity> findAllEntitiesConnectedTo(String entityName) {
        Entity e = findEntityByName(entityName).get();
        return e.getAllRelationships().stream()
                .map(r -> (r.getOwner() == e) ? r.getTarget() : r.getOwner())
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
    
    protected void show(String name) {
        if (sameLine) ok();
        System.out.println("");
        System.out.print("show " + name + " ...");
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
    
    public void showAllEntitiesConnectedTo(String entityName) {
        show("all entities connected with " + entityName);

        findAllEntitiesConnectedTo(entityName).stream()
                .forEach(e -> log(e.getName()));
        
        endTest();
    }
    
    public void allEntitisMustHaveADisplayFieldExcept(String ... exception) {
        test("all entities must have only 1 display field");
        
        List<String> exemptedList = Arrays.asList(exception);
        
        entities.values().forEach( e -> {
            List<String> displayFieldList = new ArrayList<>();
            e.getDataTypes().forEach(r -> {
                if (r.isDisplay()) {
                    displayFieldList.add(r.getName());
                    if (displayFieldList.size() != 1) {
                        error("Entity", e.getName(), 
                                "has more than 1 display field: ",
                                displayFieldList.toString());
                    }
                }
            });
            if (displayFieldList.isEmpty() && !exemptedList.contains(e.getName())) {
                warning("Entity", e.getName(), "does not have display");
            }
        });
        
        log("Exceptions: " + 
                exemptedList.stream().collect(Collectors.joining(", ")));
        
        endTest();
    }
    
    void allConnectedMustHaveNameFieldExcept(String entityName, 
            String fieldName, String... exceptions) {
        test("all connected to " + entityName + 
                " must have field named " + fieldName + 
                " except " + Arrays.toString(exceptions));

        findEntityByName(entityName).ifPresentOrElse(( Entity detail) -> {
            detail.getAllRelationships().stream()
                    .map(r -> r.getOwner())
                    .filter(e -> !Arrays.stream(exceptions)
                            .anyMatch(a -> a.equals(e.getName())) )
                    .filter(e -> e != detail)
                    .peek(e -> log("checking " + e.getName())) 
                    .filter(e -> e.getFieldByName(fieldName).isEmpty())
                    .forEach(e -> error("Entity missing ", e.getName()));

        }, () -> error(entityName + " not found!"));
        
        endTest();
    }
    
    void findAllEntitiesWithFieldName(String fieldName) {
        show("all entities with field named " + fieldName);

        entities.values().stream()
            .filter(e -> !e.getFieldByName(fieldName).isEmpty())
            .forEach(e -> log("Entity", e.getName()));

        endTest();
    }
        
    void allConnectedEntitiesMustHaveRelationNamedTheSame() {
        
        entities.values().forEach(e -> {
            String entityName = e.getName();
            String fieldName = "" + Character.toLowerCase(entityName.charAt(0)) +
                    entityName.substring(1);
            allConnectedMustHaveRelationNameExcept(entityName, fieldName);
        });
    }
    
    void allConnectedMustHaveRelationNameExcept(String entityName, 
            String fieldName, String... exceptions) {
        test("all connected to " + entityName + 
                " must have the relation named " + fieldName + 
                " except " + Arrays.toString(exceptions));

        findEntityByName(entityName).ifPresentOrElse(( Entity entity) -> {
            entity.getAllRelationships().stream()
                    .filter(r -> !r.getRelationshipType().equals(RelationshipType.OneToMany))
                    .map(r -> r.getOwner())
                    .filter(e -> !Arrays.stream(exceptions)
                            .anyMatch(a -> a.equals(e.getName())) )
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

        findEntityByName(entityName).ifPresentOrElse(( Entity entity) -> {
            entity.getAllRelationships().stream()
                    .filter(r -> !Arrays.stream(exceptions)
                            .anyMatch(e -> e.equals(r.getOwner().getName())) )
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
            e.getMapIdConnectedEntityList().forEach(c -> {
                if (equalIdSet.contains(c)) {
                    error("circular @MapId with ", e.getName(), " and ", c.getName());
                }
                equalIdSet.add(c);
            });
        });
    }
    
    void allFieldsMustHaveValidation(String fieldName, String validation) {
        test("all fields " + fieldName + " must have validation " + validation);

        entities.values().forEach( e -> {
            Optional<DataTypeRef> opt = e.getFieldByName(fieldName);
            if (opt.isPresent()) {
                DataTypeRef dataType = opt.get();
                log("checking ", e.getName());
                String dataVal = dataType.getValidation();
                if (dataVal == null || !dataVal.contains(validation)) {
                    error(validation, " is not present");
                }
            }
        });
        
        endTest();
    }
}
