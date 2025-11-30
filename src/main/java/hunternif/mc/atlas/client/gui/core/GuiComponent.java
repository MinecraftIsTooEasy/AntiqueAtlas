package hunternif.mc.atlas.client.gui.core;

import net.minecraft.FontRenderer;
import net.minecraft.GuiScreen;
import net.minecraft.Minecraft;
import net.minecraft.RenderHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiComponent extends GuiScreen {
    protected int properWidth;
    protected int properHeight;
    protected int contentWidth;
    protected int contentHeight;
    private GuiComponent parent = null;
    private final List<GuiComponent> children = new CopyOnWriteArrayList();
    private boolean sizeIsInvalid = false;
    private boolean isClipped = false;
    protected boolean isMouseOver = false;
    private boolean interceptsMouse = true;
    private boolean interceptsKeyboard = true;
    private boolean hasHandledKeyboard = false;
    private boolean hasHandledMouse = false;
    private boolean blocksScreen = false;
    private int guiX = 0;
    private int guiY = 0;
    private final HoveringTextInfo hoveringTextInfo = new HoveringTextInfo();

    public void setGuiCoords(int x, int y) {
        int dx = x - this.guiX;
        int dy = y - this.guiY;
        this.guiX = x;
        this.guiY = y;
        for (GuiComponent child : this.children) {
            child.offsetGuiCoords(dx, dy);
        }
        if (this.parent != null) {
            if (dx != 0 || dy != 0) {
                this.parent.invalidateSize();
            }
        }
    }

    public final void setRelativeCoords(int x, int y) {
        if (this.parent != null) {
            setGuiCoords(this.parent.getGuiX() + x, this.parent.getGuiY() + y);
        } else {
            setGuiCoords(x, y);
        }
    }

    public final void setRelativeX(int x) {
        if (this.parent != null) {
            setGuiCoords(this.parent.getGuiX() + x, this.guiY);
        } else {
            setGuiCoords(x, this.guiY);
        }
    }

    public final void setRelativeY(int y) {
        if (this.parent != null) {
            setGuiCoords(this.guiX, this.parent.getGuiY() + y);
        } else {
            setGuiCoords(this.guiX, y);
        }
    }

    public final void offsetGuiCoords(int dx, int dy) {
        setGuiCoords(this.guiX + dx, this.guiY + dy);
    }

    public final void setCentered() {
        validateSize();
        if (this.parent == null) {
            setGuiCoords((this.width - getWidth()) / 2, (this.height - getHeight()) / 2);
        } else {
            setRelativeCoords((this.parent.getWidth() - getWidth()) / 2, (this.parent.getHeight() - getHeight()) / 2);
        }
    }

    public int getGuiX() {
        return this.guiX;
    }

    public int getGuiY() {
        return this.guiY;
    }

    public int getRelativeX() {
        return this.parent == null ? this.guiX : this.guiX - this.parent.guiX;
    }

    public int getRelativeY() {
        return this.parent == null ? this.guiY : this.guiY - this.parent.guiY;
    }

    public void setSize(int width, int height) {
        this.properWidth = width;
        this.properHeight = height;
        this.contentWidth = width;
        this.contentHeight = height;
        invalidateSize();
    }

    public GuiComponent addChild(GuiComponent child) {
        doAddChild(null, child, null);
        return child;
    }

    public GuiComponent addChildInfrontOf(GuiComponent inFrontOf, GuiComponent child) {
        doAddChild(inFrontOf, child, null);
        return child;
    }

    public GuiComponent addChildBehind(GuiComponent behind, GuiComponent child) {
        doAddChild(null, child, behind);
        return child;
    }

    private void doAddChild(GuiComponent inFrontOf, GuiComponent child, GuiComponent behind) {
        if (child == null || this.children.contains(child) || this.parent == child) {
            return;
        }
        int i = this.children.indexOf(inFrontOf);
        if (i == -1) {
            int j = this.children.indexOf(behind);
            if (j == -1) {
                this.children.add(child);
            } else {
                this.children.add(j, child);
            }
        } else {
            this.children.add(i + 1, child);
        }
        child.parent = this;
        child.setGuiCoords(this.guiX, this.guiY);
        if (this.mc != null) {
            child.setWorldAndResolution(this.mc, this.width, this.height);
        }
        invalidateSize();
    }

    public GuiComponent removeChild(GuiComponent child) {
        if (child != null && this.children.contains(child)) {
            child.parent = null;
            this.children.remove(child);
            invalidateSize();
            onChildClosed(child);
        }
        return child;
    }

    public void removeAllChildren() {
        this.children.clear();
        invalidateSize();
    }

    public GuiComponent getParent() {
        return this.parent;
    }

    public List<GuiComponent> getChildren() {
        return this.children;
    }

    public void setInterceptMouse(boolean value) {
        this.interceptsMouse = value;
        this.allowUserInput = (!this.interceptsMouse) | (!this.interceptsKeyboard);
    }

    public void setInterceptKeyboard(boolean value) {
        this.interceptsKeyboard = value;
        this.allowUserInput = (!this.interceptsMouse) | (!this.interceptsKeyboard);
    }

    @Override // net.minecraft.client.gui.GuiScreen
    public void handleInput() {
        ListIterator<GuiComponent> iter = this.children.listIterator(this.children.size());
        while (iter.hasPrevious()) {
            GuiComponent child = iter.previous();
            if (child.blocksScreen) {
                child.handleInput();
                this.isMouseOver = false;
                return;
            }
        }
        if (this.interceptsMouse) {
            while (Mouse.next()) {
                handleMouseInput();
            }
        }
        if (this.interceptsKeyboard) {
            while (Keyboard.next()) {
                handleKeyboardInput();
            }
        }
    }

    protected void mouseHasBeenHandled() {
        this.hasHandledMouse = true;
    }

    protected void keyboardHasBeenHandled() {
        this.hasHandledKeyboard = true;
    }

    protected void setBlocksScreen(boolean value) {
        this.blocksScreen = value;
    }

    @Override
    public void handleMouseInput() {
        boolean handled = false;
        this.isMouseOver = false;
        ListIterator<GuiComponent> iter = this.children.listIterator(this.children.size());
        while (iter.hasPrevious()) {
            GuiComponent child = iter.previous();
            child.handleMouseInput();
            if (child.hasHandledMouse) {
                child.hasHandledMouse = false;
                handled = true;
            }
        }
        if (!handled) {
            this.isMouseOver = isMouseInRegion(getGuiX(), getGuiY(), getWidth(), getHeight());
            super.handleMouseInput();
        }
    }

    @Override
    public void handleKeyboardInput() {
        boolean handled = false;
        ListIterator<GuiComponent> iter = this.children.listIterator(this.children.size());
        while (iter.hasPrevious()) {
            GuiComponent child = iter.previous();
            child.handleKeyboardInput();
            if (child.hasHandledKeyboard) {
                child.hasHandledKeyboard = false;
                handled = true;
            }
        }
        if (!handled) {
            super.handleKeyboardInput();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        super.drawScreen(mouseX, mouseY, partialTick);
        for (GuiComponent child : this.children) {
            if (!child.isClipped) {
                child.drawScreen(mouseX, mouseY, partialTick);
            }
        }
        if (this.hoveringTextInfo.shouldDraw) {
            drawHoveringText2(this.hoveringTextInfo.lines, this.hoveringTextInfo.x, this.hoveringTextInfo.y, this.hoveringTextInfo.font);
            this.hoveringTextInfo.shouldDraw = false;
        }
    }

    @Override
    public void onGuiClosed() {
        for (GuiComponent child : this.children) {
            child.onGuiClosed();
        }
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        for (GuiComponent child : this.children) {
            child.updateScreen();
        }
        super.updateScreen();
        if (this.sizeIsInvalid) {
            validateSize();
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        for (GuiComponent child : this.children) {
            child.setWorldAndResolution(mc, width, height);
        }
    }

    public int getWidth() {
        return this.contentWidth;
    }

    public int getHeight() {
        return this.contentHeight;
    }

    protected void setClipped(boolean value) {
        this.isClipped = value;
    }

    protected void invalidateSize() {
        this.sizeIsInvalid = true;
        if (this.parent != null) {
            this.parent.invalidateSize();
        }
    }

    protected void validateSize() {
        int leftmost = Integer.MAX_VALUE;
        int rightmost = Integer.MIN_VALUE;
        int topmost = Integer.MAX_VALUE;
        int bottommost = Integer.MIN_VALUE;
        for (GuiComponent child : this.children) {
            int x = child.getGuiX();
            if (x < leftmost) {
                leftmost = x;
            }
            int childWidth = child.getWidth();
            if (x + childWidth > rightmost) {
                rightmost = x + childWidth;
            }
            int y = child.getGuiY();
            if (y < topmost) {
                topmost = y;
            }
            int childHeight = child.getHeight();
            if (y + childHeight > bottommost) {
                bottommost = y + childHeight;
            }
        }
        this.contentWidth = Math.max(this.properWidth, rightmost - leftmost);
        this.contentHeight = Math.max(this.properHeight, bottommost - topmost);
        this.sizeIsInvalid = false;
    }

    protected boolean isMouseInRegion(int left, int top, int width, int height) {
        int mouseX = getMouseX();
        int mouseY = getMouseY();
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY < top + height;
    }

    protected boolean isMouseInRadius(int x, int y, int radius) {
        int mouseX = getMouseX();
        int mouseY = getMouseY();
        return mouseX >= x - radius && mouseX < x + radius && mouseY >= y - radius && mouseY < y + radius;
    }

    protected void drawHoveringText2(List<String> lines, int x, int y, FontRenderer font) {
        if (!lines.isEmpty()) {
            boolean stencilEnabled = GL11.glIsEnabled(2960);
            if (stencilEnabled) {
                GL11.glDisable(2960);
            }
            RenderHelper.disableStandardItemLighting();
            int k = 0;
            for (String s : lines) {
                int l = font.getStringWidth(s);
                if (l > k) {
                    k = l;
                }
            }
            int i1 = x + 12;
            int j1 = y - 12;
            int k1 = 8;
            if (lines.size() > 1) {
                k1 = 8 + 2 + ((lines.size() - 1) * 10);
            }
            if (i1 + k > this.width) {
                i1 -= 28 + k;
            }
            if (j1 + k1 + 6 > this.height) {
                j1 = (this.height - k1) - 6;
            }
            drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, -267386864, -267386864);
            drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, -267386864, -267386864);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, -267386864, -267386864);
            drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, -267386864, -267386864);
            drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, -267386864, -267386864);
            int j2 = ((1347420415 & 16711422) >> 1) | (1347420415 & (-16777216));
            drawGradientRect(i1 - 3, (j1 - 3) + 1, (i1 - 3) + 1, ((j1 + k1) + 3) - 1, 1347420415, j2);
            drawGradientRect(i1 + k + 2, (j1 - 3) + 1, i1 + k + 3, ((j1 + k1) + 3) - 1, 1347420415, j2);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, (j1 - 3) + 1, 1347420415, 1347420415);
            drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);
            for (int k2 = 0; k2 < lines.size(); k2++) {
                String s1 = lines.get(k2);
                font.drawStringWithShadow(s1, i1, j1, -1);
                if (k2 == 0) {
                    j1 += 2;
                }
                j1 += 10;
            }
            if (stencilEnabled) {
                GL11.glEnable(2960);
            }
        }
    }

    public GuiComponent getTopLevelParent() {
        GuiComponent guiComponent = this;
        while (true) {
            GuiComponent component = guiComponent;
            if (component.parent != null) {
                guiComponent = component.parent;
            } else {
                return component;
            }
        }
    }

    protected void drawTooltip(List<String> lines, FontRenderer font) {
        GuiComponent topLevel = getTopLevelParent();
        topLevel.hoveringTextInfo.lines = lines;
        topLevel.hoveringTextInfo.x = getMouseX();
        topLevel.hoveringTextInfo.y = getMouseY();
        topLevel.hoveringTextInfo.font = font;
        topLevel.hoveringTextInfo.shouldDraw = true;
    }

    private static class HoveringTextInfo {
        List<String> lines;
        int x, y;
        FontRenderer font;
        /** Whether to draw this hovering text during rendering current frame.
         * This flag is reset to false after rendering finishes. */
        boolean shouldDraw = false;
    }

    public void close() {
        if (this.parent != null) {
            this.parent.removeChild(this);
        } else {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    protected void onChildClosed(GuiComponent child) {
    }

    protected void drawCenteredString(String text, int y, int color, boolean dropShadow) {
        int length = this.fontRenderer.getStringWidth(text);
        this.fontRenderer.drawString(text, (this.width - length) / 2, y, color, dropShadow);
    }

    protected int getMouseX() {
        return (Mouse.getX() * this.width) / this.mc.displayWidth;
    }

    protected int getMouseY() {
        return (this.height - ((Mouse.getY() * this.height) / this.mc.displayHeight)) - 1;
    }
}
