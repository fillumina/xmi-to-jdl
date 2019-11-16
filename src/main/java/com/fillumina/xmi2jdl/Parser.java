package com.fillumina.xmi2jdl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author fra
 */
public class Parser {
    
    private final ReadXMLFileUsingSaxparser hb;
    private final SAXParserFactory parserfactory;

    public Parser() {
        this.hb = new ReadXMLFileUsingSaxparser();
        this.parserfactory = SAXParserFactory.newInstance();
    }
    
    public void writeToOutputStream(OutputStream os) {
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writeToAppendable(writer);
    }
    
    public void writeToAppendable(Appendable buf) {
        try {
            hb.print(buf);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private SAXParser createParser() {
        try {
            return parserfactory.newSAXParser();
        } catch (ParserConfigurationException | SAXException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Parser parseString(String xmi) {
        InputSource inputSource = new InputSource( new StringReader(xmi));
        return parseInputSource(inputSource);
    }

    public Parser parseInputStream(InputStream is) {
        try {
            createParser().parse(is, hb);
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public Parser parseFilename(String uri) {
        try {
            createParser().parse(uri, hb);
            hb.consolidate();
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public Parser parseFile(File f) {
        try {
            createParser().parse(f, hb);
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    public Parser parseInputSource(InputSource is) {
        try {
            createParser().parse(is, hb);
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        writeToAppendable(buf);
        return buf.toString();
    }
}
