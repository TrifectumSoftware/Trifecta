package trifectumsoftware.trifecta.client;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import trifectumsoftware.trifecta.client.gui.TerminalGui;

@SideOnly(Side.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return;
        }
        if (KeyBindings.OPEN_TERMINAL.isPressed()) {
            TerminalGui.open();
        }
    }
}
