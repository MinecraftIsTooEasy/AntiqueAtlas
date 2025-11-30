package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMarkerInList extends GuiToggleButton {
    public static final int FRAME_SIZE = 34;
    private final String markerType;

    public GuiMarkerInList(String markerType) {
        this.markerType = markerType;
        setSize(34, 34);
    }

    public String getMarkerType() {
        return this.markerType;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        AtlasRenderHelper.drawFullTexture(isSelected() ? Textures.MARKER_FRAME_ON : Textures.MARKER_FRAME_OFF, getGuiX(), getGuiY(), 34, 34);
        ResourceLocation texture = MarkerTextureMap.instance().getTexture(this.markerType);
        if (texture != null) {
            AtlasRenderHelper.drawFullTexture(texture, getGuiX() + 1, getGuiY() + 1, 32, 32);
        }
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
