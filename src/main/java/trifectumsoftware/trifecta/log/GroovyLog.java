package trifectumsoftware.trifecta.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;

import trifectumsoftware.trifecta.Trifecta;

public final class GroovyLog {

    public static final GroovyLog INSTANCE = new GroovyLog();

    private static final int MAX_LINES = 2000;
    private static final int MAX_STACK_FRAMES = 40;
    private static final Object LOCK = new Object();
    private static final Deque<String> LINES = new ArrayDeque<>();
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private static PrintWriter file;
    private static boolean fileFailed;

    private GroovyLog() {}

    public void info(String message, Object... args) {
        log("INFO", null, message, args);
    }

    public void warn(String message, Object... args) {
        log("WARN", null, message, args);
    }

    public void error(String message, Object... args) {
        log("ERROR", null, message, args);
    }

    public void error(Throwable throwable, String message, Object... args) {
        log("ERROR", throwable, message, args);
    }

    public static List<String> snapshot() {
        synchronized (LOCK) {
            return new ArrayList<>(LINES);
        }
    }

    private void log(String level, Throwable throwable, String message, Object... args) {
        synchronized (LOCK) {
            String line = "[" + TIME_FORMAT.format(new Date()) + "] [" + level + "] " + format(message, args);
            add(line);
            if (throwable != null) {
                add("    " + throwable);
                StackTraceElement[] trace = throwable.getStackTrace();
                for (int i = 0; i < trace.length && i < MAX_STACK_FRAMES; i++) {
                    add("        at " + trace[i]);
                }
                if (trace.length > MAX_STACK_FRAMES) {
                    add("        ... " + (trace.length - MAX_STACK_FRAMES) + " more");
                }
            }
            if ("ERROR".equals(level)) {
                Trifecta.LOG.error(line);
            } else {
                Trifecta.LOG.info(line);
            }
        }
    }

    private static void add(String text) {

        for (String line : text.split("\n", -1)) {
            LINES.addLast(line);
            while (LINES.size() > MAX_LINES) {
                LINES.removeFirst();
            }
            writeToFile(line);
        }
    }

    private static void writeToFile(String line) {
        if (file == null && !fileFailed) {
            File target = new File("logs", "groovy.log");
            File parent = target.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory()) {
                fileFailed = true;
                Trifecta.LOG.error("Could not create {} - script file logging to logs/groovy.log is disabled", parent);
                return;
            }
            try {
                file = new PrintWriter(new FileWriter(target, false));
            } catch (IOException e) {
                fileFailed = true;
                Trifecta.LOG.error("Could not open logs/groovy.log - script file logging is disabled", e);
                return;
            }
        }
        if (file != null) {
            file.println(line);
            file.flush();
        }
    }

    private static String format(String message, Object... args) {
        if (message == null) {
            return "null";
        }
        if (args == null || args.length == 0) {
            return message;
        }
        StringBuilder out = new StringBuilder(message.length() + 32);
        int arg = 0;
        int from = 0;
        while (arg < args.length) {
            int index = message.indexOf("{}", from);
            if (index < 0) {
                break;
            }
            out.append(message, from, index)
                .append(args[arg++]);
            from = index + 2;
        }
        out.append(message, from, message.length());
        return out.toString();
    }
}
