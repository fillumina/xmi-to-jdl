package com.fillumina.xmi2jdl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public enum Pagination {
    pagination("pagination"),
    infinite("infinite-scroll");

    private final String value;

    public static Pagination parse(String s) {
        if (s == null) {
            return null;
        }
        for (Pagination p : values()) {
            if (s.contains(p.name())) {
                return p;
            }
        }
        return null;
    }

    Pagination(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
