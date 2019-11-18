package com.fillumina.xmi2jdl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DataType {

    private final String id;
    private final String name;
    private final String comment;
    private final String validation;

    public DataType(String id, String name, String comment, String validation) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.validation = validation;
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

    public String getValidation() {
        return validation;
    }

    @Override
    public String toString() {
        return name;
    }
}
