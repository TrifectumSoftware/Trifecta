package trifectumsoftware.trifecta.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.Scrollbar;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import trifectumsoftware.trifecta.Trifecta;
import trifectumsoftware.trifecta.log.GroovyLog;

@SideOnly(Side.CLIENT)
public final class TerminalGui {

    private static final float SCREEN_COVER = 0.9f;
    private static final int MAX_VISIBLE_LINES = 500;

    private TerminalGui() {}

    public static void open() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        int textWidth = (int) (resolution.getScaledWidth() * SCREEN_COVER) - 16;

        LogListWidget logView = new LogListWidget();
        logView.sizeRel(1f, 1f)
            .padding(4);

        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.texture(Scrollbar.VANILLA);
        logView.scrollDirection(scrollData);

        List<String> lines = GroovyLog.snapshot();
        int first = Math.max(0, lines.size() - MAX_VISIBLE_LINES);
        for (int i = lines.size() - 1; i >= first; i--) {
            logView.child(new LogLineWidget(lines.get(i), textWidth).widthRel(1f));
        }

        ModularPanel panel = ModularPanel.defaultPanel("terminal")
            .sizeRel(SCREEN_COVER, SCREEN_COVER)
            .resizer(
                r -> r.relativeToScreen()
                    .anchor(Alignment.Center));
        panel.child(logView);

        ClientGUI.open(new ModularScreen(Trifecta.MODID, panel));
    }

    private static final class LogListWidget extends ListWidget<IWidget, LogListWidget> {
    }

    private static final class LogLineWidget extends TextWidget<LogLineWidget> {

        LogLineWidget(String text, int maxWidth) {
            super(IKey.str(text));
            maxWidth(maxWidth);
        }
    }
}
