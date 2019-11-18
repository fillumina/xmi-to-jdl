package com.fillumina.xmi2jdl;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Reads an XMI file exported by <a href='https://umbrello.kde.org/'>Umbrello</a>
 * and produce a 
 * <a href='https://www.jhipster.tech/jdl/'>JHipster domain language</a> file.
 * 
 * Relationships are ManyToOne by default and the owner is the entity
 * containing the actual field (the FK on the db).
 *
 * Field validation can be added within curly brackets {} in comments and will
 * be parsed and removed from the actual comment.
 * 
 * Current available validations:
 * <ul>
 * <li><b>Entity</b><br>
 * One of:
 * <ul>
 * <li><code>ManyToOne</code> (default if omitted)
 * <li><code>OneToMany</code>
 * <li><code>ManyToMany</code>
 * <li><code>OneToOne</code> eventually followed by <code>with jpaDerivedIdentifier</code>
 * </ul>
 * eventually with <code>unidirectional</code> added;
 * <p>
 * Any of the following (no parameters):
 * <ul>
 * <li><code>skipClient</code> doesn't build the client
 * <li><code>skipServer</code> doesn't build the server
 * <li><code>filter</code> adds advanced search filters to the server API
 * <li><code>pagination</code> or <code>infinite-scroll</code> pagination types
 * </ul>
 * <li><b>Attribute</b><br>
 * <ul>
 * <li><code>required</code>
 * <li><code>display</code> it's the field to display when referenced
 * from another entity (there can be only one such field in an entity)
 * <li>any validation valid for the field type:
 * <ul>
 * <li>String:  required, minlength(2), maxlength(33), pattern, unique
 * <li>numbers: required, min, max, unique
 * <li>blobs:  	required, minbytes, maxbytes, unique
 * </ul>
 * </ul>
 * </ul>
 * Constants are supported, just use the tag {substitutions} in the first
 * line of  a note and put a subtitution per line in there with the format:
 * <pre><code>
 * KEY=VALUE
 * </code></pre>
 * There is no quotes and the first = separates key and value.
 * <p>
 * These are some useful substitutions:
 * <ul>
 * <li>EMAIL_PATTERN: /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/
 * <li>SDI_PATTERN: /[a-zA-z]{7}/
 * </ul>
 * 
 * JDL supports number constants so MINLENGTH, MAXLENGTH and such can be 
 * used and initialized like that in the JH file (no support here, must be
 * done manually):
 * <pre><code>
 * MINLENGTH = 20
 * </code></pre>
 *
 * @see https://github.com/jhipster/jdl-samples
 * @see https://www.jhipster.tech/jdl/
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class App {

    public static void main(String[] args) throws IOException, SAXException,
            ParserConfigurationException {
        var filename = args[0];
        if (filename == null) {
            System.err.println("filename argument missing!");
        } else {
            new Parser().parseFilename(filename)
                    .exec(new JdlProducer(System.out));
        }
    }
}
