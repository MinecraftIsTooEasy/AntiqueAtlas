package hunternif.mc.atlas.client.gui.core;

import hunternif.mc.atlas.util.AtlasRenderHelper;

public class GuiHScrollbar extends AGuiScrollbar {
    public GuiHScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    @Override
    protected void drawAnchor() {
        AtlasRenderHelper.drawTexturedRect(this.texture, getGuiX() + this.anchorPos, getGuiY(), 0, 0, this.capLength, this.textureHeight, this.textureWidth, this.textureHeight);
        AtlasRenderHelper.drawTexturedRect(this.texture, getGuiX() + this.anchorPos + this.capLength, getGuiY(), this.capLength, 0, this.textureBodyLength, this.textureHeight, this.textureWidth, this.textureHeight, this.bodyTextureScale, 1.0d);
        AtlasRenderHelper.drawTexturedRect(this.texture, ((getGuiX() + this.anchorPos) + this.anchorSize) - this.capLength, getGuiY(), this.textureWidth - this.capLength, 0, this.capLength, this.textureHeight, this.textureWidth, this.textureHeight);
    }

    @Override
    protected int getTextureLength() {
        return this.textureWidth;
    }

    @Override
    protected int getScrollbarLength() {
        return getWidth();
    }

    @Override
    protected int getViewportSize() {
        return this.viewport.getWidth();
    }

    @Override
    protected int getContentSize() {
        return this.viewport.contentWidth;
    }

    @Override
    protected int getMousePos(int mouseX, int mouseY) {
        return mouseX - getGuiX();
    }

    @Override
    protected void updateContentPos() {
        this.viewport.content.setRelativeCoords(-this.scrollPos, this.viewport.content.getRelativeY());
    }

    @Override
    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        setSize(getWidth(), textureHeight);
    }
}
