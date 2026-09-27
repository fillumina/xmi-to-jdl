package com.fillumina.xmi2jdl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Checks that the JDL written for the test diagrams is accepted by JHipster 9.
 * <p>
 * JHipster 6 and 7 accepted a trailing {@code display} option on a
 * relationship and a {@code with jpaDerivedIdentifier} option. JHipster 9
 * rejects both: the display field now goes inside the braces and a
 * relationship pointing at an entity JHipster provides is marked with
 * {@code builtInEntity}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JdlCompatibilityTest {

    private static final String[] DIAGRAMS = {
        "company.xmi", "class-diagram.xmi", "roles-class-diagram.xmi"
    };

    /** Set to a node_modules folder holding generator-jhipster to run the
     *  parser test as well, for example
     *  {@code mvn test -Djdl.parser.dir=../node_modules}. */
    private static final String PARSER_DIR_PROPERTY = "jdl.parser.dir";

    private static final Pattern LEGACY_DISPLAY_OPTION =
            Pattern.compile("to\\s+\\w+(\\{[^}]*})?\\s+display\\s*$", Pattern.MULTILINE);

    private static final Pattern LEGACY_MAP_ID_OPTION =
            Pattern.compile("to\\s+\\w+(\\{[^}]*})?\\s+with\\s+jpaDerivedIdentifier");

    @ParameterizedTest
    @ValueSource(strings = { "company.xmi", "class-diagram.xmi", "roles-class-diagram.xmi" })
    public void shouldNotWriteTheLegacyDisplayOption(String diagram) {
        String body = withoutComments(jdlFor(diagram));
        assertFalse(LEGACY_DISPLAY_OPTION.matcher(body).find(),
                () -> diagram + " still writes the removed display option:\n" + body);
    }

    @ParameterizedTest
    @ValueSource(strings = { "company.xmi", "class-diagram.xmi", "roles-class-diagram.xmi" })
    public void shouldNotWriteTheLegacyDerivedIdentifierOption(String diagram) {
        String body = withoutComments(jdlFor(diagram));
        assertFalse(LEGACY_MAP_ID_OPTION.matcher(body).find(),
                () -> diagram + " still writes the removed derived identifier"
                        + " option:\n" + body);
    }

    @Test
    public void shouldWriteTheDisplayFieldInsideTheBraces() {
        assertTrue(jdlFor("class-diagram.xmi")
                .contains("Contact{address(email) required} to Address{contact(name)}"),
                "the display field must go inside the braces");
    }

    @Test
    public void shouldMarkRelationshipsToJhipsterEntitiesAsBuiltIn() {
        String jdl = withoutComments(jdlFor("class-diagram.xmi"));
        assertTrue(jdl.contains("to User with builtInEntity"),
                () -> "a relationship to an entity JHipster provides needs the"
                        + " builtInEntity option:\n" + jdl);
    }

    @Test
    public void shouldReportADroppedDerivedIdentifierInTheErrorsSection() {
        String jdl = jdlFor("class-diagram.xmi");
        int errors = jdl.indexOf("// ERRORS");
        assertTrue(errors > 0, "the JDL must have an errors section");
        assertTrue(jdl.substring(errors).contains("jpaDerivedIdentifier"),
                "the dropped derived identifier option must be reported to the user");
    }

    @Test
    public void shouldBeAcceptedByTheJhipsterParser(@TempDir Path dir) throws Exception {
        assumeTrue(nodeIsAvailable(), "node is not on the PATH");
        String parserDir = System.getProperty(PARSER_DIR_PROPERTY);
        assumeTrue(parserDir != null && !parserDir.isEmpty(),
                "set -D" + PARSER_DIR_PROPERTY + " to a node_modules folder holding"
                        + " generator-jhipster to run this test");
        assumeTrue(Files.isDirectory(Path.of(parserDir)),
                () -> parserDir + " is not a folder");

        List<Path> written = new java.util.ArrayList<>();
        for (int i = 0; i < DIAGRAMS.length; i++) {
            Path file = dir.resolve("diagram-" + i + ".jdl");
            Files.writeString(file, jdlFor(DIAGRAMS[i]));
            written.add(file);
        }
        String script = resourcePath("/jhipster-jdl-import.mjs");

        ProcessBuilder builder = new ProcessBuilder(nodeCommand(script, parserDir, written));
        builder.redirectErrorStream(true);
        Process process = builder.start();
        String output = new String(process.getInputStream().readAllBytes());
        assertTrue(process.waitFor(2, TimeUnit.MINUTES), "the parser did not finish");
        assertTrue(process.exitValue() == 0, () -> "JHipster refused the JDL:\n" + output);
    }

    private static List<String> nodeCommand(String script, String parserDir, List<Path> files) {
        List<String> command = new java.util.ArrayList<>();
        command.add("node");
        command.add(script);
        command.add(parserDir);
        files.forEach(f -> command.add(f.toString()));
        return command;
    }

    private static boolean nodeIsAvailable() {
        try {
            Process process = new ProcessBuilder("node", "--version")
                    .redirectErrorStream(true).start();
            process.getInputStream().readAllBytes();
            return process.waitFor(1, TimeUnit.MINUTES) && process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private static String resourcePath(String name) throws Exception {
        return Path.of(JdlCompatibilityTest.class.getResource(name).toURI()).toString();
    }

    /** Returns the JDL without the comment lines, so that only real JDL is
     *  checked: the ERRORS section names the removed options on purpose. */
    private static String withoutComments(String jdl) {
        return jdl.lines()
                .filter(l -> !l.strip().startsWith("//"))
                .reduce(new StringBuilder(), (b, l) -> b.append(l).append('\n'), StringBuilder::append)
                .toString();
    }

    private static String jdlFor(String filename) {
        InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
        StringBuilder out = new StringBuilder();
        new Parser(false).parseInputStream(input).exec(new JdlProducer(out));
        return out.toString();
    }
}
