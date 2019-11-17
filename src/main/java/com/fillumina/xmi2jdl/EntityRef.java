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
    private final boolean unidirectional;

    public EntityRef(Entity owner,
            Entity target,
            String attributeName,
            CommentParser parser) {
        super(attributeName, parser);
        this.owner = owner;
        this.target = target;
        this.attributeName = attributeName;

        String v = getValidation();
        if (v == null) {
            this.validation = null;
            this.unidirectional = false;
            this.relationship = Relationship.OneToMany;
        } else {
            Relationship rel = Relationship.ManyToOne;
            for (Relationship r : Relationship.values()) {
                if (v.contains(r.name())) {
                    v = v.replace(r.name(), "").trim();
                    rel = r;
                    break;
                }
            }
            if (v.contains("unidirectional")) {
                v = v.replace("unidirectional", "");
                this.unidirectional = true;
            } else {
                this.unidirectional = false;
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

        if (unidirectional) {
            String targetDisplayField = target.getDisplayField();

            buf
                .append('\t')
                .append(owner.getName())
                .append("{")
                .append(attributeName);

            if (targetDisplayField != null) {
                buf.append('(').append(targetDisplayField).append(')');
            }

            buf
                .append("} to ")
                .append(target.getName());
            
        } else {

            String targetDisplayField = target.getDisplayField();
            String ownerDisplayField = owner.getDisplayField();

            buf
                .append('\t')
                .append(target.getName())
                .append("{")
                .append(Character.toLowerCase(owner.getName().charAt(0)) +
                        owner.getName().substring(1));

            if (ownerDisplayField != null) {
                buf.append('(').append(ownerDisplayField).append(')');
            }

            buf
                .append("} to ")
                .append(owner.getName())
                .append("{")
                .append(attributeName);

            if (targetDisplayField != null) {
                buf.append('(').append(targetDisplayField).append(')');
            }

            buf.append("}");
        }

        if (validation != null) {
            buf.append(' ').append(validation);
        }

        buf.append(System.lineSeparator());
    }
}
