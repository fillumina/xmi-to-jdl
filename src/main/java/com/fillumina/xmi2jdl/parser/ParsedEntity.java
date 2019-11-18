package com.fillumina.xmi2jdl.parser;

import com.fillumina.xmi2jdl.DataType;
import com.fillumina.xmi2jdl.DataTypeRef;
import com.fillumina.xmi2jdl.Entity;
import com.fillumina.xmi2jdl.EntityRef;
import com.fillumina.xmi2jdl.Reference;
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
    private final List<Reference> references = new ArrayList<>();

    private Entity entity;

    public ParsedEntity(String id, String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

    public void addAttribute(ParsedAttribute attribute) {
        this.attributes.add(attribute);
    }
    
    public Entity createEntity(Map<String,String> substitutions) {
        CommentParser cp = 
                new CommentParser(substitutions, comment );
        entity = new Entity(id, name, cp.getComment(), cp.getValidation(), 
                references);
        return entity;
    }
        
    /** All Entities must be created first */
    public void fillEntityAttributes(
            Map<String, DataType> dataTypes,
            Map<String, ParsedEntity> parsedEntities,
            Map<String, String> substitutions) {
        attributes.forEach(a -> {
            final String type = a.getType();
            CommentParser cp = 
                    new CommentParser(substitutions, a.getComment() );
            DataType dataType = dataTypes.get(type);
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
                references.add(dataTypeRef);
            } else {
                ParsedEntity target = parsedEntities.get(type);
                if (target != null) {
                    EntityRef entityRef = new EntityRef(
                            entity, target.entity,
                            a.getAttributeName(), 
                            cp.getComment(), cp.getValidation() );
                    references.add(entityRef);
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
