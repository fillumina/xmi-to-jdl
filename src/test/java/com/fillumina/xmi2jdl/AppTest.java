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
// TODO try to determine if a color is light or dark
// TODO rewrite to have a ColorSizeVariant class
public class AppTest {

    private static final String FILENAME = "class_diagram.xmi";
    
    public static void main(String[] args) throws IOException, SAXException,
            ParserConfigurationException {
        System.out.println("/*");
        System.out.println("\n\nFile: " + FILENAME);
        System.out.println("Date: " + (new Date().toString()) + "\n\n");
        System.out.println("*/");
        
        InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("class-diagram.xmi");
        
        //new Parser().parseInputStream(inputStream)
        new Parser().parseFile(new File("/home/fra/Development/Work/Emporia/ClassDiagram/extended_class_diagram_final_40.xmi"))
        //new Parser().parseFile(new File("/home/fra/Development/Work/Emporia/jhipster-test3/shop.xmi"))
                    .exec(new JdlProducer(System.out))
                    .exec(new EntityDiagramValidator());

    }
}
