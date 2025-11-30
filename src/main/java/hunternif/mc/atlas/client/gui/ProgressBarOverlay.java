package hunternif.mc.atlas.client.gui;

import net.minecraft.FontRenderer;
import net.minecraft.Minecraft;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;

public class ProgressBarOverlay implements ExportUpdateListener {
    private final int barWidth;
    private final int barHeight;
    private int completedWidth;
    private String status;
    private final FontRenderer font = Minecraft.getMinecraft().fontRenderer;

    public ProgressBarOverlay(int barWidth, int barHeight) {
        this.barWidth = barWidth;
        this.barHeight = barHeight;
    }

    @Override
    public void setStatusString(String status) {
        this.status = status;
    }

    @Override
    public void update(float percentage) {
        if (percentage < 0.0f) {
            percentage = 0.0f;
        }
        if (percentage > 1.0f) {
            percentage = 1.0f;
        }
        this.completedWidth = Math.round(percentage * this.barWidth);
    }

    public void draw(int x, int y) {
        int statusWidth = this.font.getStringWidth(this.status);
        this.font.drawStringWithShadow(this.status, x + ((this.barWidth - statusWidth) / 2), y, 16777215);
        int y2 = y + 14;
        GL11.glDisable(3553);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(8421504);
        tessellator.addVertex(x, y2, 0.0d);
        tessellator.addVertex(x, y2 + this.barHeight, 0.0d);
        tessellator.addVertex(x + this.barWidth, y2 + this.barHeight, 0.0d);
        tessellator.addVertex(x + this.barWidth, y2, 0.0d);
        tessellator.setColorOpaque_I(8454016);
        tessellator.addVertex(x, y2, 0.0d);
        tessellator.addVertex(x, y2 + this.barHeight, 0.0d);
        tessellator.addVertex(x + this.completedWidth, y2 + this.barHeight, 0.0d);
        tessellator.addVertex(x + this.completedWidth, y2, 0.0d);
        tessellator.draw();
        GL11.glEnable(3553);
    }

    public void reset() {
        this.completedWidth = 0;
    }
}
