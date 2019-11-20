package com.fillumina.xmi2jdl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Enumeration extends DataType implements Comparable<Enumeration> {

    private static class Literal {
        final String value;
        final String comment;

        public Literal(String name, String comment) {
            // Enum values are always UPPER CASE in JDL
            this.value = name.trim().toUpperCase();
            this.comment = comment;
        }
    }
    
    private final List<Literal> literals = new ArrayList<>();


    public Enumeration(String name, String id, 
            String comment, String validation) {
        super(id, name, comment, validation);
    }

    public void addLiteral(String name, String comment) {
        literals.add(new Literal(name, comment));
    }

    public void appendEnumeration(Appendable appendable) {
        var buf = new AppendableWrapper(appendable);
        
        var comment = getComment();
        buf.ifNotNull(comment).writeln("/** ", comment, " */");

        buf.writeln("enum ", getName(), " {");

        boolean first = true;
        for (Literal l : literals) {
            if (!first) {
                buf.writeln(", ");
            } else {
                first = false;
            }
            buf.ifNotNull(l.comment).writeln("\t/** ", l.comment, " */");
            buf.write("\t", l.value);
        }

        buf.writeln().writeln("}").writeln().writeln();
    }

    @Override
    public int compareTo(Enumeration o) {
        return getName().compareTo(o.getName());
    }
}
