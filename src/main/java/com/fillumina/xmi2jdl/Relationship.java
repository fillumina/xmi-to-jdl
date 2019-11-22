package com.fillumina.xmi2jdl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Relationship extends Reference implements Comparable<Relationship> {

    private final Entity owner;
    private final Entity target;
    private final String attributeName;
    private final String validation;
    private final RelationshipType relationship;
    private final boolean unidirectional;

    public Relationship(Entity owner,
            Entity target,
            String attributeName,
            String comment, 
            String validation) {
        super(attributeName, comment, validation);
        this.owner = owner;
        this.target = target;
        this.attributeName = attributeName;

        if (validation == null) {
            this.validation = null;
            this.unidirectional = false;
            this.relationship = RelationshipType.OneToMany;
        } else {
            String v = validation;
            RelationshipType rel = RelationshipType.ManyToOne;
            for (RelationshipType r : RelationshipType.values()) {
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

    @Override
    public String getValidation() {
        return this.validation;
    }
    
    public RelationshipType getRelationshipType() {
        return relationship;
    }

    public Entity getOwner() {
        return owner;
    }

    public Entity getTarget() {
        return target;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isUnidirectional() {
        return unidirectional;
    }
    
    @Override
    public void append(Appendable appendable) {
        var buf = new AppendableWrapper(appendable);
        var c = getComment();
        if (c != null) {
            buf.writeln("\t/** ", c, " */");
        }

        if (unidirectional) {
            var targetDisplayField = target.getDisplayField();
            buf.write("\t", owner.getName(), "{", attributeName);
            buf.ifNotNull(targetDisplayField).write("(", targetDisplayField, ")");
            buf.write("} to ", target.getName());
            
        } else {

            var targetDisplayField = target.getDisplayField();
            var ownerDisplayField = owner.getDisplayField();

            buf.write("\t", target.getName(), "{", objName(owner));

            buf.ifNotNull(ownerDisplayField).write("(", ownerDisplayField, ")");

            buf.write("} to ", owner.getName(), "{", attributeName);

            buf.ifNotNull(targetDisplayField).write("(", targetDisplayField, ")");

            buf.write("}");
        }

        buf.ifNotNull(validation).write(" ", validation);

        buf.writeln();
    }

    public void appendDetail(Entity owner, Appendable appendable) {
        var buf = new AppendableWrapper(appendable);
        boolean isOwner = getOwner() == owner;
        switch (getRelationshipType()) {
            case OneToOne:
                if (isOwner) {
                    buf.write("1:1 ", getAttributeName(), " ---- ", 
                            getTarget().getName());
                } else {
                    buf.write("1:1 ---- ", getOwner().getName(),
                            "(", getAttributeName(), ") ");
                }
                break;
            case ManyToMany:
                if (isOwner) {
                    buf.write("N:N ", getAttributeName(), " <--> ", 
                            getTarget().getName());
                } else {
                    buf.write("N:N <--> ", getOwner().getName(),
                            "(", getAttributeName(), ") ");
                }
                break;
            case OneToMany:
                if (isOwner && getTarget() == owner) {
                    buf.write("1:N ", getAttributeName(), "(self reference) ");

                } else {
                    if (isOwner) {
                        var arrow = isUnidirectional() ? " |--> ":" ---> ";
                        buf.write("N:1 ", getAttributeName(), arrow, 
                                getTarget().getName());
                    } else {
                        var arrow = isUnidirectional() ? " <--| ":" <--- ";
                        buf.write("1:N", arrow, getOwner().getName(),
                                "(", getAttributeName(), ") ");
                    }
                }
                break;
            case ManyToOne:
                if (isOwner && getTarget() == owner) {
                    buf.write("N:1 ", getAttributeName(),
                            "(self reference)");

                } else {
                    if (isOwner) {
                        var arrow = isUnidirectional() ? " |--> ":" ---> ";
                        buf.write("N:1 ", getAttributeName(), arrow, 
                                getTarget().getName());
                    } else {
                        var arrow = isUnidirectional() ? " <--| ":" <--- ";
                        buf.write("1:N", arrow, getOwner().getName(),
                                "(", getAttributeName(), ") ");
                    }
                }
                break;
        }
        buf.write(getValidation());
        buf.ifNotNull(getComment()).write("\t// ", getComment());
        buf.writeln();
    }
    
    private String objName(Entity owner) {
        return Character.toLowerCase(owner.getName().charAt(0)) +
                owner.getName().substring(1);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        append(buf);
        return buf.toString();
    }

    @Override
    public int compareTo(Relationship o) {
        if (getOwner().equals(o.getOwner())) {
            return getTarget().getName().compareTo(o.getTarget().getName());
        } else {
            return getOwner().getName().compareTo(o.getOwner().getName());
        }
    }
    
}
