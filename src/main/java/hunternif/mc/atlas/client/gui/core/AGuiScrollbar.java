package hunternif.mc.atlas.client.gui.core;

import net.minecraft.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class AGuiScrollbar extends GuiComponent {
    protected ResourceLocation texture;
    protected int textureWidth;
    protected int textureHeight;
    protected int capLength;
    protected int textureBodyLength;
    private static int scrollStep = 18;
    protected int anchorSize;
    protected final GuiViewport viewport;
    protected boolean visible = false;
    private boolean isDragged = false;
    private boolean wasClicking = false;
    private boolean usesWheel = true;
    private float contentRatio = 1.0f;
    private float scrollRatio = 0.0f;
    protected int anchorPos = 0;
    protected double bodyTextureScale = 1.0d;
    protected int scrollPos = 0;

    protected abstract int getTextureLength();

    protected abstract int getScrollbarLength();

    protected abstract int getViewportSize();

    protected abstract int getContentSize();

    protected abstract int getMousePos(int i, int i2);

    protected abstract void drawAnchor();

    protected abstract void updateContentPos();

    protected abstract void setScrollbarWidth(int i, int i2);

    public AGuiScrollbar(GuiViewport viewport) {
        this.viewport = viewport;
    }

    public void setTexture(ResourceLocation texture, int width, int height, int capLength) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        this.capLength = capLength;
        this.textureBodyLength = getTextureLength() - (capLength * 2);
        setScrollbarWidth(width, height);
    }

    public void setUsesWheel(boolean value) {
        this.usesWheel = value;
    }

    public void updateContent() {
        this.contentRatio = getViewportSize() / getContentSize();
        this.visible = this.contentRatio < 1.0f;
        updateAnchorSize();
        updateAnchorPos();
    }

    public void setScrollPos(int scrollPos) {
        this.viewport.content.validateSize();
        this.viewport.validateSize();
        doSetScrollPos(scrollPos);
    }

    private void doSetScrollPos(int scrollPos) {
        int scrollPos2 = Math.max(0, Math.min(scrollPos, getContentSize() - getViewportSize()));
        this.scrollPos = scrollPos2;
        this.scrollRatio = scrollPos2 / (getContentSize() - getViewportSize());
        updateAnchorPos();
    }

    public void setScrollRatio(float scrollRatio) {
        this.viewport.content.validateSize();
        this.viewport.validateSize();
        doSetScrollRatio(scrollRatio);
    }

    private void doSetScrollRatio(float scrollRatio) {
        if (scrollRatio < 0.0f) {
            scrollRatio = 0.0f;
        }
        if (scrollRatio > 1.0f) {
            scrollRatio = 1.0f;
        }
        this.scrollRatio = scrollRatio;
        this.scrollPos = Math.round(scrollRatio * (getContentSize() - getViewportSize()));
        updateAnchorPos();
    }

    @Override
    public void handleMouseInput() {
        int wheelMove;
        super.handleMouseInput();
        if (this.usesWheel && (wheelMove = Mouse.getEventDWheel()) != 0 && this.visible) {
            int wheelMove2 = wheelMove > 0 ? -1 : 1;
            doSetScrollPos(this.scrollPos + (wheelMove2 * scrollStep));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            this.isDragged = false;
            return;
        }
        boolean mouseDown = Mouse.isButtonDown(0);
        if (!this.wasClicking && mouseDown && this.isMouseOver) {
            this.isDragged = true;
        }
        if (!mouseDown) {
            this.isDragged = false;
        }
        this.wasClicking = mouseDown;
        if (this.isDragged) {
            doSetScrollRatio((getMousePos(mouseX, mouseY) - (this.anchorSize / 2)) / (getScrollbarLength() - this.anchorSize));
        }
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawAnchor();
        GL11.glDisable(3042);
    }

    private void updateAnchorSize() {
        this.anchorSize = Math.max(this.capLength * 2, Math.round(Math.min(1.0f, this.contentRatio) * getScrollbarLength()));
        this.bodyTextureScale = (this.anchorSize - (this.capLength * 2)) / this.textureBodyLength;
    }

    private void updateAnchorPos() {
        this.anchorPos = Math.round(this.scrollRatio * (getViewportSize() - this.anchorSize));
        updateContentPos();
    }
}
