package com.fillumina.xmi2jdl;

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
        return entity = 
                new Entity(id, name, new CommentParser(substitutions, comment));
    }
    
    /** All Entities must be created first */
    public void fillEntityAttributes(
            Map<String, DataType> dataTypes,
            Map<String, Entity> entities,
            Map<String, String> substitutions) {
        attributes.forEach(a -> {
            DataType dataType = dataTypes.get(a.getType());
            if (dataType != null) {
                if ("undef".equals(dataType.getName())) {
                    throw new RuntimeException("for entity " + name +
                            " attribute " + a.getAttributeName() +
                            " is of undefined type!");
                }
                DataTypeRef dataTypeRef = new DataTypeRef(
                        dataType,
                        a.getAttributeName(), 
                        new CommentParser(substitutions, a.getComment() ) );
                entity.addReference(dataTypeRef);
            } else {
                Entity e = entities.get(a.getType());
                if (e != null) {
                    EntityRef entityRef = new EntityRef(entity, e,
                            a.getAttributeName(), 
                            new CommentParser(substitutions, a.getComment()) );
                    entity.addReference(entityRef);
                } else {
                    throw new RuntimeException(
                            "Referred entity not found for attribute '" +
                            a.getAttributeName() + 
                            "' of entity " + entity.getName());
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
