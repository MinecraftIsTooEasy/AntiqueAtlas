package hunternif.mc.atlas.client.gui.core;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.ResourceLocation;

public class GuiCursor extends GuiComponent {
    private ResourceLocation texture;
    private int textureWidth;
    private int textureHeight;
    private int pointX;
    private int pointY;

    public void setTexture(ResourceLocation texture, int width, int height, int pointX, int pointY) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        AtlasRenderHelper.drawFullTexture(this.texture, mouseX - this.pointX, mouseY - this.pointY, this.textureWidth, this.textureHeight);
    }
}
