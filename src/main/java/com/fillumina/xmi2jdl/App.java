package com.fillumina.xmi2jdl;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
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
