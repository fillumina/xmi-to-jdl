package com.fillumina.xmi2jdl.parser;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class ParsedAttribute {
    
    private final String attributeName;
    private final String targetId;
    private String comment;

    public ParsedAttribute(String attributeName, String targetId, String comment) {
        this.attributeName = attributeName;
        this.targetId = targetId;
        this.comment = comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getAttributeName() {
        return attributeName;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getComment() {
        return comment;
    }
}
