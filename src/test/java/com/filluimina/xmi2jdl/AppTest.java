package com.filluimina.xmi2jdl;

import com.fillumina.xmi2jdl.App;
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
            "/home/fra/Development/Work/Emporia/extended_class_diagram_final_6.xmi"});

    }

}
