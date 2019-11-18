package com.fillumina.xmi2jdl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Enumeration extends DataType implements Comparable<Enumeration> {

    private final List<String> values = new ArrayList<>();


    public Enumeration(String name, String id, 
            String comment, String validation) {
        // Enum values are always UPPER CASE in JDL
        super(id, name, comment, validation);
    }

    public void addLiteral(String literal) {
        values.add(literal.trim().toUpperCase());
    }

    public void appendEnumeration(Appendable appendable) {
        var buf = new AppendableWrapper(appendable);
        
        String comment = getComment();
        buf.ifNotNull(comment).writeln("/** ", comment, " */");

        buf.writeln("enum ", getName(), " {").write('\t');

        boolean first = true;
        for (String s : values) {
            if (!first) {
                buf.write(", ");
            } else {
                first = false;
            }
            buf.write(s);
        }

        buf.writeln().writeln("}").writeln().writeln();
    }

    @Override
    public int compareTo(Enumeration o) {
        return getName().compareTo(o.getName());
    }
}
