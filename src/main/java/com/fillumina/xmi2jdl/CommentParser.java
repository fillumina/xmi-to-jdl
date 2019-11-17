package com.fillumina.xmi2jdl;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CommentParser {
    private final String comment;
    private final String validation;

    public CommentParser(String str) {
        this(null, str);
    }

    public CommentParser(Map<String,String> substitutions, String str) {
        if (str == null) {
            comment = null;
            validation = null;
        } else {
            int start = str.indexOf('{');
            if (start != -1) {
                int end = str.indexOf('}', start);
                String c = (str.substring(0, start).trim() + " " +
                        str.substring(end+1).trim()).trim();
                if (c.isBlank()) {
                    comment = null;
                } else {
                    comment = c;
                }
                validation = applySubstitutions(substitutions, 
                        str.substring(start+1, end));
            } else {
                comment = str;
                validation = null;
            }
        }
        if (comment != null && comment.contains("{")) {
            throw new RuntimeException("comment contains two validations: " + str);
        }
    }
    
    private String applySubstitutions(Map<String,String> substitutions, String text) {
        if (substitutions == null || text == null || 
                text.isBlank() || substitutions.isEmpty()) {
            return text;
        }
        String result = text;
        for (Map.Entry<String,String> e : substitutions.entrySet()) {
            if (text.contains(e.getKey())) {
                result = result.replace(e.getKey(), e.getValue());
            }
        }
        return result;
    }

    public String getComment() {
        return comment;
    }

    public String getValidation() {
        return validation;
    }

    @Override
    public String toString() {
        return "{" + "comment=" + comment + ", validation=" + validation + '}';
    }
}
