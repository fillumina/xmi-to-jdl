package com.fillumina.xmi2jdl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Options {
    private String str;

    public Options(String str) {
        this.str = str;
    }

    public boolean contains(final String name) {
        if (str == null) {
            return false;
        }
        if (str.contains(name)) {
            str = str.replace(name, "");
            return true;
        }
        return false;
    }

    public String getValue() {
        return str;
    }
}
