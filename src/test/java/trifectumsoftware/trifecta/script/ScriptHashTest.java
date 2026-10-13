package trifectumsoftware.trifecta.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class ScriptHashTest {

    private static final String EMPTY_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

    @Test
    public void emptyFolderHashesToTheEmptyMd5() throws Exception {
        Path dir = Files.createTempDirectory("trifecta-hash-empty");
        ScriptHash.Hash hash = ScriptHash.compute(dir.toFile());
        assertEquals(EMPTY_MD5, hash.hash);
        assertEquals(0, hash.fileCount);
    }

    @Test
    public void hashIsIndependentOfFileCreationOrder() throws Exception {
        Path first = Files.createTempDirectory("trifecta-hash-a");
        write(first, "b.groovy", "log.info('b')");
        write(first, "a.groovy", "log.info('a')");

        Path second = Files.createTempDirectory("trifecta-hash-b");
        write(second, "a.groovy", "log.info('a')");
        write(second, "b.groovy", "log.info('b')");

        assertEquals(ScriptHash.compute(first.toFile()).hash, ScriptHash.compute(second.toFile()).hash);
    }

    @Test
    public void contentChangesChangeTheHash() throws Exception {
        Path dir = Files.createTempDirectory("trifecta-hash-c");
        write(dir, "a.groovy", "log.info('a')");
        String before = ScriptHash.compute(dir.toFile()).hash;
        write(dir, "a.groovy", "log.info('changed')");
        assertNotEquals(before, ScriptHash.compute(dir.toFile()).hash);
    }

    @Test
    public void nestedScriptsAreCountedAndOtherFilesIgnored() throws Exception {
        Path dir = Files.createTempDirectory("trifecta-hash-d");
        write(dir, "postInit/hello.groovy", "log.info('hello')");
        write(dir, "notes.txt", "not a script");

        assertEquals(1, ScriptHash.compute(dir.toFile()).fileCount);
    }

    private static void write(Path root, String relative, String content) throws Exception {
        Path file = root.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }
}
