package trifectumsoftware.trifecta.script;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import trifectumsoftware.trifecta.log.GroovyLog;

public final class ScriptHash {

    private static Hash cached;

    private ScriptHash() {}

    public static synchronized String hash() {
        return cached().hash;
    }

    public static synchronized int fileCount() {
        return cached().fileCount;
    }

    private static Hash cached() {
        if (cached == null) {
            cached = compute(ScriptLoader.SCRIPT_DIR);
        }
        return cached;
    }

    static Hash compute(File root) {
        List<File> files = new ArrayList<>();
        collect(root, files);
        files.sort((a, b) -> relative(root, a).compareTo(relative(root, b)));

        MessageDigest digest = md5();
        for (File file : files) {
            digest.update(relative(root, file).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            try {
                digest.update(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                GroovyLog.INSTANCE
                    .error(e, "Could not read {} while hashing scripts - the script hash is incomplete", file);
            }
            digest.update((byte) 0);
        }
        return new Hash(toHex(digest.digest()), files.size());
    }

    private static void collect(File dir, List<File> out) {
        File[] entries = dir.listFiles();
        if (entries == null) {
            if (dir.exists()) {
                GroovyLog.INSTANCE.error("Could not list scripts in {} - the script hash is incomplete", dir);
            }
            return;
        }
        for (File entry : entries) {
            if (entry.isDirectory()) {
                collect(entry, out);
            } else if (entry.getName()
                .endsWith(".groovy")) {
                    out.add(entry);
                }
        }
    }

    private static String relative(File root, File file) {
        return root.toPath()
            .relativize(file.toPath())
            .toString()
            .replace('\\', '/');
    }

    private static MessageDigest md5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 is not available", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder out = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            out.append(Character.forDigit((b >> 4) & 0xF, 16));
            out.append(Character.forDigit(b & 0xF, 16));
        }
        return out.toString();
    }

    static final class Hash {

        final String hash;
        final int fileCount;

        Hash(String hash, int fileCount) {
            this.hash = hash;
            this.fileCount = fileCount;
        }
    }
}
