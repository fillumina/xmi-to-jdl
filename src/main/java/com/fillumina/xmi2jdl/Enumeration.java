package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Enumeration extends DataType implements Comparable<Enumeration> {

    private final List<String> values = new ArrayList<>();


    public Enumeration(String name, String id, CommentParser parser) {
        // Enum values are always UPPER CASE in JDL
        super(id, name, parser);
    }

    public void addLiteral(String literal) {
        values.add(literal.trim().toUpperCase());
    }

    public void appendEnumeration(Appendable buf) throws IOException {
        String comment = getComment();
        if (comment != null) {
            buf.append("/** ").append(comment).append(" */")
                    .append(System.lineSeparator());
        }

        buf
                .append("enum ")
                .append(getName())
                .append(" {")
                .append(System.lineSeparator())
                .append('\t');

        boolean first = true;
        for (String s : values) {
            if (!first) {
                buf.append(", ");
            } else {
                first = false;
            }
            buf.append(s);
        }

        buf.append(System.lineSeparator()).append('}')
                .append(System.lineSeparator()).append(System.lineSeparator());
    }

    @Override
    public int compareTo(Enumeration o) {
        return getName().compareTo(o.getName());
    }
}
