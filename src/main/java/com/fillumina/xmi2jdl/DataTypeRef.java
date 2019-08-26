package com.fillumina.xmi2jdl;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DataTypeRef extends Reference {

    private final DataType dataType;

    public DataTypeRef(DataType dataType, String name, String comment) {
        super(name, comment);
        this.dataType = dataType;
    }

    @Override
    public void append(Appendable buf) throws IOException {
        String comment = getComment();
        if (comment != null) {
            buf.append("\t/** ").append(comment).append(" */")
                    .append(System.lineSeparator());
        }

        buf
                .append('\t')
                .append(getName())
                .append(" ")
                .append(dataType.toString());

        String validation = getValidation();
        if (validation != null) {
            buf.append(" ").append(validation);
        }

        buf.append(System.lineSeparator());
    }
}
