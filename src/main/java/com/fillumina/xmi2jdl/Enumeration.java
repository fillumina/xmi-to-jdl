package com.fillumina.xmi2jdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if (comment != null || isAnyCommentInLiterals()) {
            buf.writeln("/**");
            buf.ifNotNull(comment).writeln(comment).writeln();
            literals.forEach(l -> buf.writeln(l.value, ": \t", l.comment));
            buf.writeln("*/");
        }

        buf.writeln("enum ", getName(), " {");
        buf.writeln("\t", literals.stream()
                .map(l -> l.value).collect(Collectors.joining(", ")))
            .writeln("}").writeln();
    }

    private boolean isAnyCommentInLiterals() {
        return literals.stream().reduce(Boolean.FALSE, 
            (Boolean t, Literal l) -> t || l.comment != null, Boolean::logicalOr);
    }
    
    @Override
    public int compareTo(Enumeration o) {
        return getName().compareTo(o.getName());
    }
    
}
