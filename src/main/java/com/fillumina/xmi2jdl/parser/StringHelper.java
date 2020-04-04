package com.fillumina.xmi2jdl.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class StringHelper {

    public static String toString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
    
    public static String merge(String a, String b) {
        if (a == null || a.isBlank()) {
            return b;
        }
        if (b == null || b.isBlank()) {
            return a;
        }
        Set<String> aset = new HashSet<>(Arrays.asList(a.split(" ")));
        Set<String> bset = new HashSet<>(Arrays.asList(b.split(" ")));
        
        aset.addAll(bset);
        
        return aset.stream().collect(Collectors.joining(" "));
    }
}
