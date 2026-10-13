package trifectumsoftware.trifecta.client;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class KeyBindings {

    public static final KeyBinding OPEN_TERMINAL = new KeyBinding(
        "key.trifecta.terminal",
        Keyboard.KEY_INSERT,
        "key.categories.trifecta");

    private KeyBindings() {}

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_TERMINAL);
    }
}
