package com.fillumina.xmi2jdl;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class Reference {

    private final String name;
    private final String comment;
    private final String validation;

    public Reference(String name, String comment) {
        this.name = name;
        CommentParser parser = new CommentParser(comment);
        this.comment = parser.getComment();
        this.validation = parser.getValidation();
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getValidation() {
        return validation;
    }

    public abstract void append(Appendable buf) throws IOException;
}
