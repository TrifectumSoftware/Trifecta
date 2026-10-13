package trifectumsoftware.trifecta.script;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trifectumsoftware.trifecta.Config;
import trifectumsoftware.trifecta.log.GroovyLog;

public final class ScriptLoader {

    public static final File SCRIPT_DIR = new File("scripts");

    private ScriptLoader() {}

    public static void runStage(String stage) {
        File stageDir = new File(SCRIPT_DIR, stage);
        if (!stageDir.isDirectory()) {
            if (!stageDir.mkdirs() && !stageDir.isDirectory()) {
                GroovyLog.INSTANCE
                    .error("Could not create script folder {} - stage '{}' will not run", stageDir.getPath(), stage);
                return;
            }
            GroovyLog.INSTANCE
                .info("Created script folder {} - put .groovy files there to run them", stageDir.getPath());
            return;
        }
        File[] files = stageDir.listFiles((dir, name) -> name.endsWith(".groovy"));
        if (files == null) {
            GroovyLog.INSTANCE
                .error("Could not list scripts in {} - stage '{}' will not run", stageDir.getPath(), stage);
            return;
        }
        if (files.length == 0) {
            GroovyLog.INSTANCE.info("Stage '{}': no scripts found in {}", stage, stageDir.getPath());
            return;
        }
        Arrays.sort(
            files,
            (a, b) -> a.getName()
                .compareTo(b.getName()));

        List<String> failed = new ArrayList<>();
        try {
            ScriptEngine engine = new ScriptEngine();
            for (File file : files) {
                if (!engine.run(file)) {
                    failed.add(file.getName());
                }
            }
        } catch (Throwable t) {
            GroovyLog.INSTANCE.error(t, "The script engine itself failed while running stage '{}'", stage);
            for (File file : files) {
                if (!failed.contains(file.getName())) {
                    failed.add(file.getName());
                }
            }
        }
        if (failed.isEmpty()) {
            GroovyLog.INSTANCE.info("Stage '{}': ran {} script(s)", stage, files.length);
            return;
        }
        String message = "Stage '" + stage
            + "': "
            + failed.size()
            + " of "
            + files.length
            + " script(s) failed: "
            + String.join(", ", failed);
        GroovyLog.INSTANCE.error(message);
        if (Config.strictScripts) {
            throw new IllegalStateException(message);
        }
    }
}
