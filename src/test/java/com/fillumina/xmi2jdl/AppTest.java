package com.fillumina.xmi2jdl;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppTest {

    public static void main(String[] args) throws IOException, SAXException,
            ParserConfigurationException {

        App.main(new String[]{
            "/home/fra/Development/Work/Emporia/ClassDiagram/extended_class_diagram_final_24.xmi"
        });

    }

}
