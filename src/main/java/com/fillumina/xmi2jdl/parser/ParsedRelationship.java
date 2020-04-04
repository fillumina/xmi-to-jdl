package com.fillumina.xmi2jdl.parser;

/**
 *
 * @author fra
 */
class ParsedRelationship {
    private String name;
    private String ownerEntityId;
    private String targetEntityId;
    private Multiplicity ownerMultiplicity;
    private Multiplicity targetMultiplicity;

    public ParsedRelationship name(final String value) {
        this.name = value;
        return this;
    }

    public ParsedRelationship ownerEntityId(final String value) {
        this.ownerEntityId = value;
        return this;
    }

    public ParsedRelationship targetEntityId(final String value) {
        this.targetEntityId = value;
        return this;
    }

    public ParsedRelationship ownerMultiplicity(final String value) {
        this.ownerMultiplicity = Multiplicity.parse(value);
        return this;
    }

    public ParsedRelationship targetMultiplicity(final String value) {
        this.targetMultiplicity = Multiplicity.parse(value);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getOwnerEntityId() {
        return ownerEntityId;
    }

    public String getTargetEntityId() {
        return targetEntityId;
    }

    public Multiplicity getOwnerMultiplicity() {
        return ownerMultiplicity;
    }

    public Multiplicity getTargetMultiplicity() {
        return targetMultiplicity;
    }
}
