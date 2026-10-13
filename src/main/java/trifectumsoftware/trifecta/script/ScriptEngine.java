package trifectumsoftware.trifecta.script;

import java.io.File;

import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import trifectumsoftware.trifecta.log.GroovyLog;

public class ScriptEngine {

    private final GroovyLog log = GroovyLog.INSTANCE;
    private final GroovyShell shell;

    public ScriptEngine() {
        Binding binding = new Binding();
        binding.setVariable("log", log);
        this.shell = new GroovyShell(ScriptEngine.class.getClassLoader(), binding);
    }

    public boolean run(File file) {
        try {
            shell.evaluate(file);
            return true;
        } catch (MultipleCompilationErrorsException e) {
            log.error("Failed to compile {}:\n{}", file.getName(), formatCompileErrors(e));
            return false;
        } catch (Throwable t) {
            log.error(t, "Script {} failed", file.getName());
            return false;
        }
    }

    private static String formatCompileErrors(MultipleCompilationErrorsException e) {
        ErrorCollector collector = e.getErrorCollector();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < collector.getErrorCount(); i++) {
            Message message = collector.getError(i);
            if (message instanceof SyntaxErrorMessage syntaxError) {
                SyntaxException cause = syntaxError.getCause();
                out.append("  ")
                    .append(cause.getLine())
                    .append(':')
                    .append(cause.getStartColumn())
                    .append(": ")
                    .append(cause.getOriginalMessage())
                    .append('\n');
            } else {
                out.append("  ")
                    .append(message)
                    .append('\n');
            }
        }
        return out.toString();
    }
}
