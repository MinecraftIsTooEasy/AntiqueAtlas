package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.Minecraft;
import net.minecraft.RenderHelper;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

public class GuiPositionButton extends GuiComponentButton {
    public static final int WIDTH = 11;
    public static final int HEIGHT = 11;

    public GuiPositionButton() {
        setSize(11, 11);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        if (isEnabled()) {
            RenderHelper.disableStandardItemLighting();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            int x = getGuiX();
            int y = getGuiY();
            if (this.isMouseOver) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
            }
            AtlasRenderHelper.drawFullTexture(Textures.BTN_POSITION, x, y, 11, 11);
            GL11.glDisable(3042);
            if (this.isMouseOver) {
                drawTooltip(Collections.singletonList("跟随该玩家"), Minecraft.getMinecraft().fontRenderer);
            }
        }
    }
}
