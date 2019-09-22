package com.fillumina.xmi2jdl;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
 * <li><code>ManyToOne</code> (default)
 * <li><code>OneToMany</code>
 * <li><code>ManyToMany</code>
 * <li><code>OneToOne</code>
 * </ul>
 * eventually with <code>unidirectional</code> added;
 * <p>
 * Any of the following (no parameters):
 * <ul>
 * <li>skipClient 
 * <li>skipServer
 * <li>noFluentMethod
 * <li>filter
 * </ul>
 * Any of the following (one parameter):
 * <ul>
 * <li>dto (mapstruct)
 * <li>service (serviceClass, serviceImpl)
 * <li>paginate (pager, pagination, infinite-scroll)
 * <li>search (elasticsearch)
 * <li>microservice (custom value)
 * <li>angularSuffix (custom value)
 * <li>clientRootFolder (custom value)
 * </ul>
 * <li><b>Attribute</b><br>
 * <ul>
 * <li><code>required</code>
 * <li><code>display</code> it's the field to display when referenced
 * from another entity (there can be only one such field in an entity)
 * <li>any validation valid for the field type:
 * <ul>
 * <li>String:  required, minlength, maxlength, pattern, unique
 * <li>numbers: required, min, max, unique
 * <li>blobs:  	required, minbytes, maxbytes, unique
 * </ul>
 * </ul>
 * </ul>
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class App {

    public static void main(String[] args) throws IOException, SAXException,
            ParserConfigurationException {

        SAXParserFactory parserfactory = SAXParserFactory.newInstance();

        SAXParser parser = parserfactory.newSAXParser();

        ReadXMLFileUsingSaxparser handler = new ReadXMLFileUsingSaxparser();

        String filename = args[0];
        if (filename == null) {
            System.err.println("filename argument missing!");
        } else {
            parser.parse(filename, handler);
            handler.print(System.out);
        }
    }

}
