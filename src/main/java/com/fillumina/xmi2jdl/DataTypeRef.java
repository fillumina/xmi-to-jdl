package com.fillumina.xmi2jdl;

import java.io.IOException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DataTypeRef extends Reference {

    private final DataType dataType;
    private final String validation;
    private final boolean display;

    public DataTypeRef(DataType dataType, String name, 
            String comment, String validation) {
        super(name, comment, validation);
        this.dataType = dataType;
        String v = getValidation();
        if (v != null && v.contains("display")) {
            this.display = true;
            this.validation = v.replace("display", "");
        } else {
            this.display = false;
            this.validation = v;
        }
    }

    public boolean isDisplay() {
        return display;
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

        if (validation != null) {
            buf.append(" ").append(validation);
        }

        buf.append(System.lineSeparator());
    }
}
