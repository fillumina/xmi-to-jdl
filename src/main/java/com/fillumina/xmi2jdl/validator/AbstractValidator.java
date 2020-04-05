package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.util.AppendableWrapper;
import com.fillumina.xmi2jdl.DataType;
import com.fillumina.xmi2jdl.DataTypeRef;
import com.fillumina.xmi2jdl.Entity;
import com.fillumina.xmi2jdl.EntityDiagram;
import com.fillumina.xmi2jdl.EntityDiagramConsumer;
import com.fillumina.xmi2jdl.Enumeration;
import com.fillumina.xmi2jdl.Enumeration.Literal;
import com.fillumina.xmi2jdl.Relationship;
import com.fillumina.xmi2jdl.RelationshipType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractValidator extends Tester 
        implements EntityDiagramConsumer {

    protected Map<String, DataType> dataTypes;
    protected Map<String, Entity> entities;
    protected Map<String, Enumeration> enumerations;

    public AbstractValidator() {
        this(System.err);
    }
    
    public AbstractValidator(AppendableWrapper appendableWrapper) {
        super(appendableWrapper);
    }
    
    public AbstractValidator(Appendable appendable) {
        super(appendable);
    }
    
    @Override
    public void consume(EntityDiagram entityDiagram) {
        initWithEntityDiagram(entityDiagram);
        
        createTests();
    }

    public void executeTests() {
        startTests();
        
        getTests().forEach(t -> {
            test(t.getName());
            t.getRunnable().run();
            endTest();
        });
        
        endTests();
    }

    public void initWithEntityDiagram(EntityDiagram entityDiagram) {
        dataTypes = entityDiagram.getDataTypes();
        entities = entityDiagram.getEntities();
        enumerations = entityDiagram.getEnumerations();
    }

    
    protected Optional<Entity> findEntityByName(String name) {
        return entities.values().stream()
                .filter(e -> Objects.equals(e.getName(), name)).findFirst();
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
    
    protected Relationship findRelationship(String entityName, 
            String relationshipName) {
        Optional<Entity> opt = findEntityByName(entityName);
        if (opt.isPresent()) {
            return opt.get().getAllRelationships().stream()
                    .filter(r -> Objects.equals(relationshipName, r.getName()))
                    .findFirst()
                    .get();
        }
        error("relationship for entity " + entityName +
                " relationship " + relationshipName + " not found");
        return null;
    }
    
    protected AssertRelationship assertRelationship(String entityName,
            String relationshipName) {
        return new AssertRelationship(this,  entityName, relationshipName);
    }
    
    protected void assertEntityNotPresent(String entityName) {
        test("entity " + entityName + " not present", () -> {
            if (findEntityByName(entityName).isPresent()) {
                error("entity " + entityName + " found");
            }
        });
    }
    
    protected void assertEntityPresent(String entityName) {
        test("entity " + entityName + " present", () -> {
            if (findEntityByName(entityName).isEmpty()) {
                error("entity " + entityName + " not found");
            }
        });
    }
    
    protected void assertEnumValues(String enumName, String... values) {
        String msg = "enum " + enumName;
        Optional<Enumeration> enumerationOpt = enumerations.values().stream()
                .filter(e -> Objects.equals(e.getName(), enumName))
                .findFirst();
        if (enumerationOpt.isEmpty()) {
            test(msg, () -> {
                error(msg + " doesn't exist");
            });
        } else {
            List<Literal> list = enumerationOpt.get().getLiterals();
            Set<String> literals = list.stream()
                    .map(l -> l.getValue())
                    .collect(Collectors.toSet());
            List<String> missing = new ArrayList<>();
            for (String v : values) {
                if (!literals.contains(v)) {
                    missing.add(v);
                }
            }
            test(msg, () -> {
                if (!missing.isEmpty()) {
                    error(msg + " has missing values " + missing.toString());
                }
            });
        }
    }
    
    protected void assertAttributeNotPresent(String entityName,
            String attributeName) {
        String msg = "enitity " + entityName + 
                " attribute " + attributeName + ": ";
        Optional<Entity> entityOpt = findEntityByName(entityName);
        if (entityOpt.isEmpty()) {
            test(msg, () -> {
                error(msg + " entity doesn't exist");
            });
        } else {
            Entity entity = entityOpt.get();
            Optional<DataTypeRef> attrOpt = entity.getFieldByName(attributeName);
            if (attrOpt.isPresent()) {
                test(msg, () -> {
                    error(msg + " attribute exists");
                });
            }
        }
    }
    
    protected AssertAttribute assertAttribute(String entityName, 
            String attributeName) {
        return new AssertAttribute(this, entityName, attributeName);
    }
    
    protected void showAllEntitiesWithFieldName(String fieldName) {
        show("all entities with field named " + fieldName);

        entities.values().stream()
            .filter(e -> !e.getFieldByName(fieldName).isEmpty())
            .forEach(e -> log("Entity", e.getName()));
    }
    
    protected AssertEntity assertEntity(String entityName) {
        return new AssertEntity(this, entityName);
    }
    
    protected void showAllEntitiesConnectedTo(String entityName) {
        show("all entities connected with " + entityName);

        findAllEntitiesConnectedTo(entityName).stream()
                .forEach(e -> log(e.getName()));
    }
        
    protected void allConnectedEntitiesMustHaveRelationNamedTheSame() {
        
        entities.values().forEach(e -> {
            String entityName = e.getName();
            String fieldName = "" + Character.toLowerCase(entityName.charAt(0)) +
                    entityName.substring(1);
            allConnectedMustHaveRelationNameExcept(entityName, fieldName);
        });
    }
    
    protected void checkAllNamesFirstCharLowerCaseAndMaxLength() {
        test("all field names against common mistakes", () -> {
            entities.values().stream()
                .forEach(e -> {
                    var entityName = e.getName();
                    e.getDataTypes().stream()
                            .map(d -> d.getName())
                            .filter(n -> Character.isUpperCase(
                                    n.charAt(0)) || n.length() > 28)
                            .forEach(n -> 
                                    error("Entity", entityName, " field ", n));
                });
        });        
    }
    
    protected void forbiddenEntityNameCheck(String ... names) {
        test("forbiddenEntityNameCheck", () -> {
            Arrays.asList(names).forEach(n -> {
                findEntitiesByRegexp(n).stream().forEach(e -> {
                    error("Bad name for entity '", e.getName() , "'");
                });
            });
        });
    }
    
    protected void allEntitisMustHaveADisplayFieldExcept(String ... exception) {
        test("all entities must have only 1 display field", () -> {
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
        });
    }
    
    protected void allConnectedMustHaveNameFieldExcept(String entityName, 
            String fieldName, String... exceptions) {
        test("all connected to " + entityName + 
                " must have field named " + fieldName + 
                " except " + Arrays.toString(exceptions), () -> {

            findEntityByName(entityName).ifPresentOrElse(( Entity detail) -> {
                detail.getAllRelationships().stream()
                        .map(r -> r.getOwner())
                        .filter(e -> !Arrays.stream(exceptions)
                                .anyMatch(a -> Objects.equals(a, e.getName())) )
                        .filter(e -> e != detail)
                        .peek(e -> log("checking " + e.getName())) 
                        .filter(e -> e.getFieldByName(fieldName).isEmpty())
                        .forEach(e -> error("Entity missing ", e.getName()));

            }, () -> error(entityName + " not found!"));
        });
    }
    
    protected void allConnectedMustHaveRelationNameExcept(String entityName, 
            String fieldName, String... exceptions) {
        test("all connected to " + entityName + 
                " must have the relation named " + fieldName + 
                " except " + Arrays.toString(exceptions), () -> {

            findEntityByName(entityName).ifPresentOrElse(( Entity entity) -> {
                entity.getAllRelationships().stream()
                        .filter(r -> ! Objects.equals(
                                r.getRelationshipType(), 
                                RelationshipType.OneToMany))
                        .map(r -> r.getOwner())
                        .filter(e -> !Arrays.stream(exceptions)
                                .anyMatch(a -> Objects.equals(a, e.getName())) )
                        .filter(e -> e != entity)
                        .peek(e -> log("checking " + e.getName())) 
                        .filter(e -> e.getRelationByName(fieldName).isEmpty())
                        .forEach(e -> error("Entity missing '" + fieldName + 
                                "': ", e.getName() ));

            }, () -> error(entityName + " not found!"));
        });
    }
    
    protected void allConnectedMustHaveUnidirectionalRelationExcept(
            String entityName, 
            String ... exceptions) {
        test("all connected to " + entityName + 
                " must have unidirectional relationship except: " + 
                Arrays.toString(exceptions), () ->  {
            findEntityByName(entityName).ifPresentOrElse(( Entity entity) -> {
                entity.getAllRelationships().stream()
                        .filter(r -> !Arrays.stream(exceptions)
                                .anyMatch(e -> Objects.equals(e, r.getOwner().getName())) )
                        .filter(r -> r.getOwner() != entity)
                        .filter(r -> ! Objects.equals(
                                r.getRelationshipType(),
                                RelationshipType.OneToMany))
                        .peek(r -> log("checking " + r.getOwner().getName() + " ..."))
                        .filter(r -> !r.isUnidirectional())
                        .forEach(r -> error("not unidirectional from " + 
                                r.getOwner().getName()));

            }, () -> error(entityName + " not found!"));
        });
    }
    
    protected void noCircularOneToOneWithMapIdRelationships() {
        test("circular 1 to 1 with @MapId", () -> {

            entities.values().forEach(e -> {
                Set<Entity> equalIdSet = new HashSet<>();
                e.getMapIdConnectedEntityList().forEach(c -> {
                    if (equalIdSet.contains(c)) {
                        error("circular @MapId with ", e.getName(), " and ", c.getName());
                    }
                    equalIdSet.add(c);
                });
            });
        });
    }
    
    protected void allFieldsMustHaveValidation(String fieldName, String validation) {
        test("all fields " + fieldName + " must have validation " + validation, 
                () -> {

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
        });
    }
}