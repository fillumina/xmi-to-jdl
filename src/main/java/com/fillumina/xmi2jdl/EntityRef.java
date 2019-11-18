package com.fillumina.xmi2jdl;

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
            String comment, 
            String validation) {
        super(attributeName, comment, validation);
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

    private String objName(Entity owner) {
        return Character.toLowerCase(owner.getName().charAt(0)) +
                owner.getName().substring(1);
    }
}
