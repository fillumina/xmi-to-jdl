package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author fra
 */
public class AppendableWrapper {
    private static final AppendableWrapper NULL = new AppendableWrapper(null);
    
    private final Appendable appendable;
    private boolean used;

    public boolean isUsed() {
        return used;
    }
    
    public AppendableWrapper(Appendable appendable) {
        this.appendable = appendable;
    }
    
    public AppendableWrapper write(String ... array) {
        if (appendable == null) return this;
        try {
            for (String s: array) {
                appendable.append(s);
                used = true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    
    public AppendableWrapper ifNotNull(Object ... array) {
        for (Object o: array) {
            if (o == null) {
                return NULL;
            }
        }
        return this;
    }

    public AppendableWrapper ifUsed() {
        return ifTrue(used);
    }
    
    public AppendableWrapper ifTrue(boolean clause) {
        if (clause) {
            return this;
        }
        return NULL;
    }
    
    public AppendableWrapper writeln(Object ... array) {
        if (appendable == null) return this;
        try {
            for (Object s: array) {
                appendable.append(Objects.toString(s));
            }
            appendable.append(System.lineSeparator());
            used = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    
    public AppendableWrapper write(char c) {
        if (appendable == null) return this;
        try {
            appendable.append(c);
            used = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    
    public Appendable getAppendable() {
        return appendable;
    }
}
