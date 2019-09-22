package com.fillumina.xmi2jdl;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CommentParser {
    private final String comment;
    private final String validation;

    public CommentParser(String str) {
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
                validation = str.substring(start+1, end);
            } else {
                comment = str;
                validation = null;
            }
        }
        if (comment != null && comment.contains("{")) {
            throw new RuntimeException("comment contains two validations: " + str);
        }
    }

    public String getComment() {
        return comment;
    }

    public String getValidation() {
        return validation;
    }
}
