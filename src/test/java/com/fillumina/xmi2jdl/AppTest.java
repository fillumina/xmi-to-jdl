package com.fillumina.xmi2jdl;

import java.io.IOException;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppTest {

    private static final String FILENAME =
        "/home/fra/Development/Work/Emporia/ClassDiagram/"
            + "extended_class_diagram_final_25.xmi";
    
    public static void main(String[] args) throws IOException, SAXException,
            ParserConfigurationException {
        System.out.println("\n\nFile: " + FILENAME);
        System.out.println("Date: " + (new Date().toString()) + "\n\n");
        
        new Parser().parseFilename(FILENAME)
                    .exec(new JdlProducer(System.out))
                    .exec(new EntityDiagramValidator());

    }
}
