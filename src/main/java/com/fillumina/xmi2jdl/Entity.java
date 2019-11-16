package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final List<Attribute> attributes = new ArrayList<>();
    private final List<Reference> references = new ArrayList<>();

    public Entity(String id, String name, String comment) {
        this.name = name;
        this.id = id;
        CommentParser parser = new CommentParser(comment);
        this.comment = parser.getComment();
        Options opt = new Options(parser.getValidation());
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

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }
    
    public void resolveReferences(
            Map<String, DataType> dataTypes,
            Map<String, Entity> entities,
            List<String> errors) {
        attributes.forEach(a -> {
            DataType dataType = dataTypes.get(a.getType());
            if (dataType != null) {
                if ("undef".equals(dataType.getName())) {
                    errors.add("for entity " + getName() +
                            " attribute " + a.getAttributeName() +
                            " is of undefined type!");
                }
                DataTypeRef dataTypeRef = new DataTypeRef(
                        dataType,
                        a.getAttributeName(), 
                        a.getComment());
                addReference(dataTypeRef);
            } else {
                Entity entity = entities.get(a.getType());
                if (entity != null) {
                    EntityRef entityRef = new EntityRef(this, entity,
                            a.getAttributeName(), a.getComment());
                    addReference(entityRef);
                } else {
                    throw new RuntimeException("Referred entity not found: " +
                            a.getAttributeName());
                }
            }
        });
    }
    
    private void addReference(Reference ref) {
        references.add(ref);
    }

    public void appendEntity(Appendable buf) throws IOException {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return;
        }

        if (skipClient) {
            buf.append("@skipClient").append(System.lineSeparator());
        }
        if (skipServer) {
            buf.append("@skipServer").append(System.lineSeparator());
        }
        if (filter) {
            buf.append("@filter").append(System.lineSeparator());
        }
        if (pagination != null) {
            buf.append("@paginate(")
                    .append(pagination.getValue())
                    .append(")")
                    .append(System.lineSeparator());
        }

        if (comment != null) {
            buf.append("/** ").append(comment).append(" */")
                    .append(System.lineSeparator());
        }

        buf
                .append("entity ")
                .append(name);

        if (hasDataTypeAttributes()) {
            buf
                    .append(" {")
                    .append(System.lineSeparator());

            for (Reference r : references) {
                if (r instanceof DataTypeRef) {
                    r.append(buf);
                }
            }
            buf.append('}');
        }
        buf.append(System.lineSeparator()).append(System.lineSeparator());
    }

    public boolean hasRelationships(Relationship rel) {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return false;
        }

        for (Reference r : references) {
            if (r instanceof EntityRef &&
                    ((EntityRef)r).getRelationship().equals(rel)) {
                return true;
            }
        }
        return false;
    }

    public void appendRelationship(Relationship rel , Appendable buf)
            throws IOException {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return;
        }

        boolean output = false;
        for (Reference r : references) {
            if (r instanceof EntityRef &&
                    ((EntityRef)r).getRelationship().equals(rel)) {
                r.append(buf);
                output = true;
            }
        }
        if (output) {
            buf.append(System.lineSeparator());
        }
    }

    private boolean hasDataTypeAttributes() {
        // User & Authority are provided by JHipster
        if ("User".equals(name) || "Authority".equals(name)) {
            return false;
        }

        for (Reference r : references) {
            if (r instanceof DataTypeRef) {
                return true;
            }
        }
        return false;
    }

    public String getDisplayField() {
        for (Reference r : references) {
            if (r instanceof DataTypeRef && ((DataTypeRef)r).isDisplay()) {
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
