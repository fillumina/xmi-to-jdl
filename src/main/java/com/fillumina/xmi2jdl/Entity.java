package com.fillumina.xmi2jdl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    private final List<Relationship> ownedRelationships;
    private final List<Relationship> allRelationships;


    public Entity(String id, String name, String comment, String validation,
            List<DataTypeRef> dataTypes, 
            List<Relationship> ownedRelationships,
            List<Relationship> allRelationships) {
        this.name = name;
        this.id = id;
        this.comment = comment;
        
        // that's a known side-effect (which is bad sorry)
        this.dataTypes = dataTypes;
        this.ownedRelationships = ownedRelationships;
        this.allRelationships = allRelationships;
        
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

    public Pagination getPagination() {
        return pagination;
    }

    public boolean isFilter() {
        return filter;
    }

    public boolean isSkipServer() {
        return skipServer;
    }

    public boolean isSkipClient() {
        return skipClient;
    }

    
    public List<DataTypeRef> getDataTypes() {
        return dataTypes;
    }

    public List<Relationship> getOwnedRelationships() {
        return ownedRelationships;
    }

    public List<Relationship> getAllRelationships() {
        return allRelationships;
    }
    
    public Optional<DataTypeRef> getFieldByName(String name) {
        return dataTypes.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst();
    }
    
    public Optional<Relationship> getRelationByName(String name) {
        return allRelationships.stream().filter( d -> d.getName().equals(name))
                .findFirst();
    }
    
    public void appendEntity(Appendable appendable)  {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return;
        }

        var buf = new AppendableWrapper(appendable);

        if (!allRelationships.isEmpty() || comment != null) {
            buf.writeln("/**");
            
            buf.ifNotNull(comment).writeln(comment).writeln();

            Collections.sort(this.allRelationships);
            
            for (var relationship : RelationshipType.values()) {
                allRelationships.stream()
                        .filter(r -> r.getRelationshipType() == relationship)
                        .forEach(r -> r.appendDetailLn(this, appendable));
            }
            buf.writeln("*/");
        }

        buf.ifTrue(skipClient).writeln("@skipClient");
        buf.ifTrue(skipServer).writeln("@skipServer");
        buf.ifTrue(filter).writeln("@filter");
        
        if (pagination != null) {
            buf.writeln("@paginate(", pagination.getValue(), ")");
        }

        buf.write("entity ", name);
        
        if (hasDataTypeAttributes()) {
            buf.writeln(" {");
            dataTypes.forEach(r -> r.append(buf.getAppendable()));
            buf.write('}');
        }
        buf.writeln().writeln();
    }
    
    public void appendRelationship(RelationshipType rel , Appendable appendable) {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return;
        }

        AtomicBoolean atLeastOne = new AtomicBoolean(false);
        
        ownedRelationships.stream()
                .filter(r -> Objects.equals(r.getRelationshipType(), rel))
                .peek(e -> atLeastOne.set(true))
                .forEach(r -> r.append(appendable));
        
        if (atLeastOne.get()) {
            new AppendableWrapper(appendable).writeln();
        }
    }

    public boolean hasRelationships(RelationshipType rel) {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return false;
        }

        return ownedRelationships.stream()
                .anyMatch(r -> Objects.equals(r.getRelationshipType(), rel));
    }

    private boolean hasDataTypeAttributes() {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return false;
        }

        return !dataTypes.isEmpty();
    }

    public List<Entity> getMapIdConnectedEntityList() {
        return ownedRelationships.stream()
                .filter(r -> r.getOwner() == this && 
                        r.getValidation() != null &&
                        r.getValidation().contains("with jpaDerivedIdentifier"))
                .map(r -> r.getTarget())
                .collect(Collectors.toList());
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        appendEntity(buf);
        return buf.toString();
    }
}
