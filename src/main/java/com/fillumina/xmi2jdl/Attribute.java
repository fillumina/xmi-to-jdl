package com.fillumina.xmi2jdl;

/**
 *
 * @author fra
 */
public class Attribute {
    private final String attributeName;
    private final String type;
    private final String comment;

    public Attribute(String attributeName, String type, String comment) {
        this.attributeName = attributeName;
        this.type = type;
        this.comment = comment;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }
}
