package trifectumsoftware.trifecta;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static boolean strictScripts = true;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        strictScripts = configuration.getBoolean(
            "strictScripts",
            Configuration.CATEGORY_GENERAL,
            strictScripts,
            "If true, a script that fails to load aborts startup instead of being skipped");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
