package com.fillumina.xmi2jdl;

/**
 * Reads a XMI file exported by Umbrello https://umbrello.kde.org/
 *
 * Relationships are ManyToOne by default and the owner is the entity
 * containing the actual field (the FK on the db).
 *
 * Field validation can be added within curly brackets {} in comments.
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
                String c = (str.substring(0, start) + str.substring(end+1)).trim();
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
    }

    public String getComment() {
        return comment;
    }

    public String getValidation() {
        return validation;
    }
}
