package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Entity implements Comparable<Entity> {

    private final String name;
    private final String id;
    private final String comment;
    private final List<Reference> references = new ArrayList<>();

    public Entity(String name, String id, String comment) {
        this.name = name;
        this.id = id;
        this.comment = comment;
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

    public void addReference(Reference ref) {
        references.add(ref);
    }

    public void appendEntity(Appendable buf) throws IOException {
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

    public void appendRelationship(Appendable buf) throws IOException {
        boolean output = false;
        for (Reference r : references) {
            if (r instanceof EntityRef) {
                r.append(buf);
                output = true;
            }
        }
        if (output) {
            buf.append(System.lineSeparator());
        }
    }

    private boolean hasDataTypeAttributes() {
        for (Reference r : references) {
            if (r instanceof DataTypeRef) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Entity o) {
        return getName().compareTo(o.getName());
    }
}