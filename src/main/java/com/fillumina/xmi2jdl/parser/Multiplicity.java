package com.fillumina.xmi2jdl.parser;

import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
enum Multiplicity {
    None(""),
    One("0..1"),
    OneRequired("1"),
    Many("*"),
    ManyRequired("1..*");
    
    private final String value;
    
    Multiplicity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    static Multiplicity parse(String s) {
        for (Multiplicity m : values()) {
            if (Objects.equals(m.getValue(), s)) {
                return m;
            }
        }
        throw new IllegalArgumentException("value not found: " + s);
    }
    
    static String getRelationAsString(Multiplicity owner, Multiplicity target) {
        switch (owner) {
            case None:
                return "";
            case One:
                switch (target) {
                    case One:          return "OneToOne";
                    case OneRequired:  return "OneToOne";
                    case Many:         return "OneToMany";
                    case ManyRequired: return "OneToMany";
                }
                break;
            case OneRequired:
                switch (target) {
                    case One:          return "OneToOne required";
                    case OneRequired:  return "OneToOne required";
                    case Many:         return "OneToMany required";
                    case ManyRequired: return "OneToMany required";
                }
                break;
            case Many:
                switch (target) {
                    case One:          return "ManyToOne";
                    case OneRequired:  return "ManyToOne";
                    case Many:         return "ManyToMany";
                    case ManyRequired: return "ManyToMany";
                }
                break;
            case ManyRequired:
                switch (target) {
                    case One:          return "ManyToOne required";
                    case OneRequired:  return "ManyToOne required";
                    case Many:         return "ManyToMany required";
                    case ManyRequired: return "ManyToMany required";
                }
                break;
        }
        throw new IllegalArgumentException();
    }
}
