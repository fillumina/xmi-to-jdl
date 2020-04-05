package com.fillumina.xmi2jdl.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StringHelper {

    public static String toString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
    
    public static String mergeTokens(String a, String b) {
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
    
    public static boolean equalTokens(String a, String b) {
        if (a == null || a.isBlank()) {
            return b == null || b.isBlank();
        }
        Set<String> aset = new HashSet<>(Arrays.asList(a.split(" ")));
        Set<String> bset = new HashSet<>(Arrays.asList(b.split(" ")));

        return aset.size() == bset.size() && aset.containsAll(bset);
    }
}
