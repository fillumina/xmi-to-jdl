package com.fillumina.xmi2jdl;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class EntityRef extends Reference {

    private final Entity owner;
    private final Entity target;
    private final String attributeName;
    private final String validation;
    private final Relationship relationship;

    public EntityRef(Entity owner,
            Entity target,
            String attributeName,
            String comment) {
        super(attributeName, comment);
        this.owner = owner;
        this.target = target;
        this.attributeName = attributeName;

        String v = getValidation();
        if (v == null) {
            this.validation = null;
            this.relationship = Relationship.ManyToOne;
        } else {
            Relationship rel = Relationship.ManyToOne;
            for (Relationship r : Relationship.values()) {
                if (v.contains(r.name())) {
                    v = v.replace(r.name(), "").trim();
                    rel = r;
                    break;
                }
            }
            this.validation = v;
            this.relationship = rel;
        }
    }

    public Relationship getRelationship() {
        return relationship;
    }

    @Override
    public void append(Appendable buf) throws IOException {
        String c = getComment();
        if (c != null) {
            buf.append("\t/** ").append(c).append(" */")
                    .append(System.lineSeparator());
        }

        buf
                .append('\t')
                .append(owner.getName())
                .append("{")
                .append(attributeName)
                .append("} to ")
                .append(target.getName());

        if (validation != null) {
            buf.append(' ').append(validation);
        }

        buf.append(System.lineSeparator());
    }
}
