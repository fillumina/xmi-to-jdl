package com.fillumina.xmi2jdl.parser;

import com.fillumina.xmi2jdl.DataType;
import com.fillumina.xmi2jdl.DataTypeRef;
import com.fillumina.xmi2jdl.Entity;
import com.fillumina.xmi2jdl.Relationship;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author fra
 */
class ParsedEntity {
    private final List<ParsedAttribute> attributes = new ArrayList<>();

    private final String id;
    private final String name;
    private final String comment;

    private final List<DataTypeRef> dataTypes = new ArrayList<>();
    private final List<Relationship> ownedRelations = new ArrayList<>();
    private final List<Relationship> allRelations = new ArrayList<>();

    private Entity entity;

    public ParsedEntity(String id, String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

    public void addAttribute(ParsedAttribute attribute) {
        this.attributes.add(attribute);
    }
    
    public void addRelationship(Relationship entityRef) {
        allRelations.add(entityRef);
    }
    
    public Entity createEntity(Map<String,String> substitutions) {
        CommentParser cp = 
                new CommentParser(substitutions, comment);
        entity = new Entity(id, name, cp.getComment(), cp.getValidation(), 
                dataTypes, ownedRelations, allRelations);
        return entity;
    }
        
    public void fillEntityAttributes(
            Map<String, DataType> dataTypeMap,
            Map<String, ParsedEntity> parsedEntitiesMap,
            Map<String, String> substitutionsMap) {
        attributes.forEach(a -> {
            final String type = a.getType();
            CommentParser cp = 
                    new CommentParser(substitutionsMap, a.getComment() );
            DataType dataType = dataTypeMap.get(type);
            if (dataType != null) {
                if ("undef".equals(dataType.getName())) {
                    throw new RuntimeException("for entity " + name +
                            " attribute " + a.getAttributeName() +
                            " is of undefined type!");
                }
                DataTypeRef dataTypeRef = new DataTypeRef(
                        dataType,
                        a.getAttributeName(), 
                        cp.getComment(), cp.getValidation() );
                dataTypes.add(dataTypeRef);
            } else {
                ParsedEntity target = parsedEntitiesMap.get(type);
                if (target != null) {
                    Relationship entityRef = new Relationship(
                            entity, target.entity,
                            a.getAttributeName(), 
                            cp.getComment(), cp.getValidation() );
                    ownedRelations.add(entityRef);
                    allRelations.add(entityRef);
                    if (target.entity != entity) {
                        target.addRelationship(entityRef);
                    }
                } else {
                    throw new RuntimeException(
                            "Referred entity not found for attribute '" +
                            a.getAttributeName() + 
                            "' of entity " + name);
                }
            }
        });
    }
        
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
