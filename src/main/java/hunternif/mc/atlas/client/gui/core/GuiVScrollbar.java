package hunternif.mc.atlas.client.gui.core;

import hunternif.mc.atlas.util.AtlasRenderHelper;

public class GuiVScrollbar extends AGuiScrollbar {
    public GuiVScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    @Override
    protected void drawAnchor() {
        AtlasRenderHelper.drawTexturedRect(this.texture, getGuiX(), getGuiY() + this.anchorPos, 0, 0, this.textureWidth, this.capLength, this.textureWidth, this.textureHeight);
        AtlasRenderHelper.drawTexturedRect(this.texture, getGuiX(), getGuiY() + this.anchorPos + this.capLength, 0, this.capLength, this.textureWidth, this.textureBodyLength, this.textureWidth, this.textureHeight, 1.0d, this.bodyTextureScale);
        AtlasRenderHelper.drawTexturedRect(this.texture, getGuiX(), ((getGuiY() + this.anchorPos) + this.anchorSize) - this.capLength, 0, this.textureHeight - this.capLength, this.textureWidth, this.capLength, this.textureWidth, this.textureHeight);
    }

    @Override
    protected int getTextureLength() {
        return this.textureHeight;
    }

    @Override
    protected int getScrollbarLength() {
        return getHeight();
    }

    @Override
    protected int getViewportSize() {
        return this.viewport.getHeight();
    }

    @Override
    protected int getContentSize() {
        return this.viewport.contentHeight;
    }

    @Override
    protected int getMousePos(int mouseX, int mouseY) {
        return mouseY - getGuiY();
    }

    @Override
    protected void updateContentPos() {
        this.viewport.content.setRelativeCoords(this.viewport.content.getRelativeX(), -this.scrollPos);
    }

    @Override
    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        setSize(textureWidth, getHeight());
    }
}
