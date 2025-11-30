package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.RenderHelper;
import org.lwjgl.opengl.GL11;

public class GuiArrowButton extends GuiComponentButton {
    public static final int WIDTH = 12;
    public static final int HEIGHT = 12;
    private static final int IMAGE_WIDTH = 24;
    private static final int IMAGE_HEIGHT = 24;
    public ArrowDirection direction;

    public enum ArrowDirection {
        UP("Up"),
        DOWN("Down"),
        LEFT("Left"),
        RIGHT("Right");

        public String description;

        ArrowDirection(String text) {
            this.description = text;
        }
    }

    public GuiArrowButton(ArrowDirection direction) {
        setSize(12, 12);
        this.direction = direction;
    }

    public static GuiArrowButton up() {
        return new GuiArrowButton(ArrowDirection.UP);
    }

    public static GuiArrowButton down() {
        return new GuiArrowButton(ArrowDirection.DOWN);
    }

    public static GuiArrowButton left() {
        return new GuiArrowButton(ArrowDirection.LEFT);
    }

    public static GuiArrowButton right() {
        return new GuiArrowButton(ArrowDirection.RIGHT);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        RenderHelper.disableStandardItemLighting();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        int x = getGuiX();
        int y = getGuiY();
        if (this.isMouseOver) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            int distanceSq = (((mouseX - x) - (getWidth() / 2)) * ((mouseX - x) - (getWidth() / 2))) + (((mouseY - y) - (getHeight() / 2)) * ((mouseY - y) - (getHeight() / 2)));
            double alpha = distanceSq < 400 ? 0.5d : Math.pow(distanceSq, -0.28d);
            GL11.glColor4d(1.0d, 1.0d, 1.0d, alpha);
        }
        int u = 0;
        int v = switch (this.direction) {
            case LEFT -> {
                u = 0;
                yield 0;
            }
            case RIGHT -> {
                u = 0;
                yield 12;
            }
            case UP -> {
                u = 12;
                yield 0;
            }
            case DOWN -> {
                u = 12;
                yield 12;
            }
        };
        AtlasRenderHelper.drawTexturedRect(Textures.BTN_ARROWS, x, y, u, v, 12, 12, 24, 24);
        GL11.glDisable(3042);
    }
}
