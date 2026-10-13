package trifectumsoftware.trifecta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import trifectumsoftware.trifecta.log.GroovyLog;

@SideOnly(Side.CLIENT)
public final class ChatNotifier {

    private ChatNotifier() {}

    public static void warn(String text) {
        GroovyLog.INSTANCE.warn(text);
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.YELLOW + "[Trifecta] " + EnumChatFormatting.RESET + text));
        }

    }
}
