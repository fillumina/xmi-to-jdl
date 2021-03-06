package com.fillumina.xmi2jdl;

import com.fillumina.xmi2jdl.validator.AbstractValidator;
import java.io.InputStream;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * 
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTest extends AbstractValidator {
    
    public AbstractTest(String filename, boolean honourPrivate, Appendable out) {
        InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(filename);
        
        new Parser(honourPrivate).parseInputStream(inputStream)
                .exec(new JdlProducer(out))
                .exec(this);

        // tests are executed twice: for the output and by junit
        
        setVerbose(false);
        setThrowAssertionError(false);
        setAppendable(out);
        
        // output
        executeTests();

        setVerbose(true);
        setThrowAssertionError(true);
        setAppendable(null);
        
        // junit...
    }

    @TestFactory
    public Iterable<DynamicTest> factory() {
        return getTests().stream()
                .map(e -> DynamicTest.dynamicTest(
                        e.getName(), 
                        () -> e.getRunnable().run()))
                .collect(Collectors.toList());
    }
}
