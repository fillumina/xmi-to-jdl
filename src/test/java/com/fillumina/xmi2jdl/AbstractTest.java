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
    
    public AbstractTest(String filename, Appendable out) {
        InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(filename);
        
        new Parser().parseInputStream(inputStream)
                .exec(new JdlProducer(out))
                .exec(this);

        // tests are executed twice: once for the output, then by junit
        
        setVerbose(false);
        setThrowAssertionError(false);
        setAppendable(out);
        
        executeTests();

        setVerbose(true);
        setThrowAssertionError(true);
        setAppendable(null);
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
