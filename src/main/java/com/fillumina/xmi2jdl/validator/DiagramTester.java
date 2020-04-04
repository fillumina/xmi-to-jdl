package com.fillumina.xmi2jdl.validator;

import com.fillumina.xmi2jdl.AppendableWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class DiagramTester {
    
    public static class DiagramTest {
        private final String name;
        private final Runnable runnable;

        public DiagramTest(String name, Runnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }

        public String getName() {
            return name;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }
    
    protected boolean sameLine;
    protected int warningCounter;
    protected boolean verbose;
    protected AppendableWrapper out;
    
    private final List<DiagramTest> tests = new ArrayList<>();
    
    private boolean throwAssertionError;
    private boolean error;
    private int errorCounter;

    public DiagramTester(AppendableWrapper appendableWrapper) {
        this.out = appendableWrapper;
    }

    public DiagramTester(Appendable appendable) {
        this.out = new AppendableWrapper(appendable);
    }

    public abstract void createTests();

    public List<DiagramTest> getTests() {
        return tests;
    }

    public void setThrowAssertionError(boolean throwAssertionError) {
        this.throwAssertionError = throwAssertionError;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setAppendable(Appendable appendable) {
        this.out = new AppendableWrapper(appendable);
    }

    protected void startTests() {
        out.writeln("/*");
        out.writeln("Executing Validator");
    }

    protected void endTests() {
        if (sameLine) {
            ok();
        }
        sameLine = false;
        out.writeln("");
        out.write("End Validator");
        if (warningCounter > 0) {
            out.write(", " + warningCounter + " WARNINGS FOUND");
        }
        if (errorCounter > 0) {
            out.write(", " + errorCounter + " ERRORS FOUND");
        }
        out.writeln("");
        out.writeln("*/");
    }

    protected void test(String name, Runnable test) {
        tests.add(new DiagramTest(name, test));
    }

    protected void test(String name) {
        if (sameLine) {
            ok();
        }
        out.writeln("");
        out.write("testing " + name + " ...");
        sameLine = true;
        error = false;
    }

    protected void show(String name) {
        if (sameLine) {
            ok();
        }
        out.writeln("");
        out.write("show " + name + " ...");
        sameLine = true;
        error = false;
    }

    protected void endTest() {
        if (error) {
            out.writeln("ERROR!");
        } else {
            ok();
        }
    }

    protected void ok() {
        out.writeln(" OK!");
        sameLine = false;
    }

    protected void log(String... message) {
        if (!verbose) {
            return;
        }
        if (sameLine) {
            out.writeln("");
        }
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        out.writeln(" . " + msg);
    }

    protected void warning(String... message) {
        if (sameLine) {
            out.writeln("");
        }
        sameLine = false;
        warningCounter++;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        out.writeln("\tWARNING: " + msg);
    }

    protected void error(String... message) {
        error = true;
        errorCounter++;
        if (sameLine) {
            out.writeln("");
        }
        sameLine = false;
        String msg = Arrays.stream(message).collect(Collectors.joining(" "));
        out.writeln("\tERROR: " + msg);
        if (throwAssertionError) {
            throw new AssertionError(msg);
        }
    }
    
}
