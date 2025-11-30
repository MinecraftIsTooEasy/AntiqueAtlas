package hunternif.mc.atlas.client.gui;

import com.google.common.collect.ImmutableMap;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;

import java.util.Collections;
import java.util.Map;

public class GuiScaleBar extends GuiComponent {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 8;
    private static Map<Double, ResourceLocation> textureMap;
    private double mapScale;

    public GuiScaleBar() {
        ImmutableMap.Builder<Double, ResourceLocation> builder = ImmutableMap.builder();
        builder.put(Double.valueOf(0.0625d), Textures.SCALEBAR_512);
        builder.put(Double.valueOf(0.125d), Textures.SCALEBAR_256);
        builder.put(Double.valueOf(0.25d), Textures.SCALEBAR_128);
        builder.put(Double.valueOf(0.5d), Textures.SCALEBAR_64);
        builder.put(Double.valueOf(1.0d), Textures.SCALEBAR_32);
        builder.put(Double.valueOf(2.0d), Textures.SCALEBAR_16);
        builder.put(Double.valueOf(4.0d), Textures.SCALEBAR_8);
        builder.put(Double.valueOf(8.0d), Textures.SCALEBAR_4);
        textureMap = builder.build();
        this.mapScale = 1.0d;
        setSize(20, 8);
    }

    public void setMapScale(double scale) {
        this.mapScale = scale;
    }

    private ResourceLocation getTexture() {
        return textureMap.get(Double.valueOf(this.mapScale));
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = getTexture();
        if (texture == null) {
            return;
        }
        AtlasRenderHelper.drawFullTexture(texture, getGuiX(), getGuiY(), 20, 8);
        if (this.isMouseOver) {
            drawTooltip(Collections.singletonList("以方块缩放"), Minecraft.getMinecraft().fontRenderer);
        }
    }
}
