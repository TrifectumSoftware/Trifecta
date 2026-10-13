package trifectumsoftware.trifecta.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import trifectumsoftware.trifecta.Config;
import trifectumsoftware.trifecta.Tags;
import trifectumsoftware.trifecta.Trifecta;
import trifectumsoftware.trifecta.network.NetworkHandler;
import trifectumsoftware.trifecta.script.ScriptLoader;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        NetworkHandler.init();

        Trifecta.LOG.info("I am " + Trifecta.NAME + " at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        ScriptLoader.runStage("postInit");
        NetworkHandler.checkScriptHash();
    }

    public void serverStarting(FMLServerStartingEvent event) {}
}
