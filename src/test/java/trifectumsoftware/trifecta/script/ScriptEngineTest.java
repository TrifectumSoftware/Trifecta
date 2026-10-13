package trifectumsoftware.trifecta.script;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;

public class ScriptEngineTest {

    private final ScriptEngine engine = new ScriptEngine();

    @Test
    public void runsValidScript() throws Exception {
        File script = writeScript(
            "ok",
            "def greet = { name -> \"Hello, ${name}!\" }\n" + "assert greet('Trifecta') == 'Hello, Trifecta!'\n"
                + "assert [1, 2, 3].collect { it * 2 } == [2, 4, 6]\n");
        assertTrue(engine.run(script));
    }

    @Test
    public void reportsCompileErrors() throws Exception {
        File script = writeScript("broken", "this is not valid groovy ((\n");
        assertFalse(engine.run(script));
    }

    @Test
    public void reportsRuntimeErrors() throws Exception {
        File script = writeScript("boom", "throw new IllegalStateException('boom')\n");
        assertFalse(engine.run(script));
    }

    private static File writeScript(String name, String source) throws Exception {
        File file = Files.createTempFile("trifecta-" + name + "-", ".groovy")
            .toFile();
        file.deleteOnExit();
        Files.write(file.toPath(), source.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
