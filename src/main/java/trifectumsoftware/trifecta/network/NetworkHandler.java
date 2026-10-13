package trifectumsoftware.trifecta.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import trifectumsoftware.trifecta.Trifecta;
import trifectumsoftware.trifecta.log.GroovyLog;
import trifectumsoftware.trifecta.network.message.MessageScriptHash;
import trifectumsoftware.trifecta.script.ScriptHash;

public final class NetworkHandler {

    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Trifecta.MODID);

    private static boolean scriptHashAvailable;

    private NetworkHandler() {}

    public static void init() {
        CHANNEL.registerMessage(MessageScriptHash.Handler.class, MessageScriptHash.class, 0, Side.CLIENT);
        FMLCommonHandler.instance()
            .bus()
            .register(new ScriptHashListener());
    }

    public static void checkScriptHash() {
        try {
            String hash = ScriptHash.hash();
            int fileCount = ScriptHash.fileCount();
            scriptHashAvailable = true;
            GroovyLog.INSTANCE.info("Script set: {} file(s), md5 {}", fileCount, hash);
        } catch (Throwable t) {
            scriptHashAvailable = false;
            GroovyLog.INSTANCE.error(t, "Could not compute the script set hash - script hash sync is disabled");
        }
    }

    public static boolean isScriptHashAvailable() {
        return scriptHashAvailable;
    }

    public static void sendScriptHashTo(EntityPlayerMP player) {
        if (!scriptHashAvailable) {
            return;
        }
        try {
            CHANNEL.sendTo(new MessageScriptHash(ScriptHash.hash(), ScriptHash.fileCount()), player);
        } catch (Throwable t) {
            scriptHashAvailable = false;
            GroovyLog.INSTANCE.error(
                t,
                "Could not send the script set hash to {} - script hash sync is disabled",
                player.getCommandSenderName());
        }
    }

    public static class ScriptHashListener {

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (!(event.player instanceof EntityPlayerMP)) {
                return;
            }
            sendScriptHashTo((EntityPlayerMP) event.player);
        }
    }
}
