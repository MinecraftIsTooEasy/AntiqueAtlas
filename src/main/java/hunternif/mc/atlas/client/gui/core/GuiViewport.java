package hunternif.mc.atlas.client.gui.core;

import net.minecraft.ScaledResolution;

import org.lwjgl.opengl.GL11;

public class GuiViewport extends GuiComponent {
    protected final GuiComponent content = new GuiComponent();
    private int screenScale;

    public GuiViewport() {
        addChild(this.content);
    }

    public GuiComponent addContent(GuiComponent child) {
        return this.content.addChild(child);
    }

    public GuiComponent removeContent(GuiComponent child) {
        return this.content.removeChild(child);
    }

    public void removeAllContent() {
        this.content.removeAllChildren();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.screenScale = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight).getScaleFactor();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        GL11.glEnable(3089);
        GL11.glScissor(getGuiX() * this.screenScale, this.mc.displayHeight - ((getGuiY() + this.properHeight) * this.screenScale), this.properWidth * this.screenScale, this.properHeight * this.screenScale);
        super.drawScreen(mouseX, mouseY, par3);
        GL11.glDisable(3089);
    }

    @Override
    public void handleMouseInput() {
        if (isMouseInRegion(getGuiX(), getGuiY(), this.properWidth, this.properHeight)) {
            super.handleMouseInput();
        }
    }

    @Override
    public int getWidth() {
        return this.properWidth;
    }

    @Override
    public int getHeight() {
        return this.properHeight;
    }

    @Override
    protected void validateSize() {
        super.validateSize();
        for (GuiComponent child : getChildren()) {
            child.setClipped(child.getGuiY() > getGuiY() + this.properHeight || child.getGuiY() + child.getHeight() < getGuiY() || child.getGuiX() > getGuiX() + this.properWidth || child.getGuiX() + child.getWidth() < getGuiX());
        }
    }
}
