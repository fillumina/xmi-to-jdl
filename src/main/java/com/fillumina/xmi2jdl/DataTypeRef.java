package com.fillumina.xmi2jdl;

import com.fillumina.xmi2jdl.util.AppendableWrapper;

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

    public DataType getDataType() {
        return dataType;
    }

    public boolean isDisplay() {
        return display;
    }

    @Override
    public void append(Appendable appendable) {
        var buf = new AppendableWrapper(appendable);
        var comment = getComment();
        
        buf.ifNotNull(comment).writeln("\t/** ", comment, " */");
        buf.ifTrue(display).writeln("\t// display");

        buf.write("\t", getName(), " ", dataType.toString());

        if (validation != null) {
            buf.write(" ", validation);
        }

        buf.writeln();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        append(buf);
        return buf.toString();
    }
    
}
