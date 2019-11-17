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

    public Reference(String name, CommentParser parser) {
        this.name = name;
        try {
            this.comment = parser.getComment();
            this.validation = parser.getValidation();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception found on " + name + " comment: " + parser, e);
        }
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public final String getValidation() {
        return validation;
    }

    public abstract void append(Appendable buf) throws IOException;
}
