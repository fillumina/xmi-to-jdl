package com.fillumina.xmi2jdl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AppTest {

    private static final String FILENAME = "class_diagram.xmi";
    
    public static void main(String[] args) throws IOException, SAXException,
            ParserConfigurationException {
        System.out.println("\n\nFile: " + FILENAME);
        System.out.println("Date: " + (new Date().toString()) + "\n\n");
        
        InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("class-diagram.xmi");
        
        new Parser().parseInputStream(inputStream)
                    .exec(new JdlProducer(System.out))
                    .exec(new EntityDiagramValidator());

    }
}
