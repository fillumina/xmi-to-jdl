package com.fillumina.xmi2jdl;

import com.fillumina.xmi2jdl.parser.ReadXMLFileUsingSaxParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Parser {
    
    private final ReadXMLFileUsingSaxParser hb;
    private final SAXParserFactory parserfactory;

    public Parser() {
        this.hb = new ReadXMLFileUsingSaxParser();
        this.parserfactory = SAXParserFactory.newInstance();
    }
    
    private SAXParser createParser() {
        try {
            return parserfactory.newSAXParser();
        } catch (ParserConfigurationException | SAXException ex) {
            throw new RuntimeException(ex);
        }
    }

    public EntityDiagram parseString(String xmi) {
        InputSource inputSource = new InputSource( new StringReader(xmi));
        return parseInputSource(inputSource);
    }

    public EntityDiagram parseInputStream(InputStream is) {
        try {
            createParser().parse(is, hb);
            hb.consolidate();
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return hb;
    }

    public EntityDiagram parseFilename(String uri) {
        try {
            createParser().parse(uri, hb);
            hb.consolidate();
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return hb;
    }

    public EntityDiagram parseFile(File f) {
        try {
            createParser().parse(f, hb);
            hb.consolidate();
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return hb;
    }

    public EntityDiagram parseInputSource(InputSource is) {
        try {
            createParser().parse(is, hb);
            hb.consolidate();
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return hb;
    }
}
