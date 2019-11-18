package com.fillumina.xmi2jdl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Entity implements Comparable<Entity> {

    private final String name;
    private final String id;
    private final String comment;
    private final Pagination pagination;
    private final boolean filter;
    private final boolean skipServer;
    private final boolean skipClient;
    private final List<DataTypeRef> dataTypes;
    private final List<EntityRef> entities;

    public Entity(String id, String name, String comment, String validation,
            List<DataTypeRef> dataTypes, List<EntityRef> entities) {
        this.name = name;
        this.id = id;
        this.comment = comment;
        
        // that's a known side-effect (which is bad sorry)
        this.dataTypes = Collections.unmodifiableList(dataTypes);
        this.entities = Collections.unmodifiableList(entities);
        
        var opt = new Options(validation);
        this.filter = opt.contains("filter");
        this.skipClient = opt.contains("skipClient");
        this.skipServer = opt.contains("skipServer");
        this.pagination = Pagination.parse(opt.getValue());
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public List<DataTypeRef> getDataTypes() {
        return dataTypes;
    }

    public List<EntityRef> getEntities() {
        return entities;
    }

    
    public void appendEntity(Appendable appendable)  {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return;
        }

        var buf = new AppendableWrapper(appendable);

        buf.ifTrue(skipClient).writeln("@skipClient");
        buf.ifTrue(skipServer).writeln("@skipServer");
        buf.ifTrue(filter).writeln("@filter");
        
        if (pagination != null) {
            buf.writeln("@paginate(", pagination.getValue(), ")");
        }

        buf.ifNotNull(comment).writeln("/** ", comment, " */");

        buf.write("entity ", name);

        if (hasDataTypeAttributes()) {
            buf.writeln(" {");
            dataTypes.forEach(r -> r.append(buf.getAppendable()));
            buf.write('}');
        }
        buf.writeln().writeln();
    }

    public void appendRelationship(Relationship rel , Appendable appendable) {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return;
        }

        AtomicBoolean atLeastOne = new AtomicBoolean(false);
        
        entities.stream()
                .filter(r -> r.getRelationship().equals(rel))
                .peek(e -> atLeastOne.set(true))
                .forEach(r -> r.append(appendable));
        
        if (atLeastOne.get()) {
            new AppendableWrapper(appendable).writeln();
        }
    }

    public boolean hasRelationships(Relationship rel) {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return false;
        }

        return entities.stream().anyMatch(r -> r.getRelationship().equals(rel));
    }

    private boolean hasDataTypeAttributes() {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return false;
        }

        return !dataTypes.isEmpty();
    }

    public String getDisplayField() {
        for (Reference r : dataTypes) {
            if (((DataTypeRef)r).isDisplay()) {
                return r.getName();
            }
        }
        return null;
    }

    @Override
    public int compareTo(Entity o) {
        return getName().compareTo(o.getName());
    }
}
