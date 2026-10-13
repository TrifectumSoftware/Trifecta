package trifectumsoftware.trifecta.script;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class GroovyPackagingTest {

    @Test
    public void orgApacheGroovyIsRelocatedAway() {
        assertThrows(
            ClassNotFoundException.class,
            () -> Class.forName("org.apache.groovy.parser.antlr4.Antlr4PluginFactory"));
    }

    @Test
    public void relocatedParserSharesTheUnrelocatedBaseClass() throws Exception {
        Class<?> relocated = Class
            .forName("trifectumsoftware.trifecta.compat.antlr4.parser.antlr4.Antlr4PluginFactory");
        Class<?> base = Class.forName("org.codehaus.groovy.control.ParserPluginFactory");
        assertSame(base, relocated.getSuperclass());
    }
}
