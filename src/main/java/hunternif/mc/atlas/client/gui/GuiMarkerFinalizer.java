package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.client.gui.core.GuiScrollingContainer;
import hunternif.mc.atlas.client.gui.core.ToggleGroup;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import net.minecraft.*;

import java.util.ArrayList;
import java.util.List;

public class GuiMarkerFinalizer extends GuiComponent {
    public static final String defaultMarker = "red_x_small";
    private World world;
    protected int atlasID;
    protected int dimension;
    protected int x;
    protected int z;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_SPACING = 4;
    private static final int TYPE_SPACING = 1;
    private static final int TYPE_BG_FRAME = 4;
    private GuiButton btnDone;
    private GuiButton btnCancel;
    private GuiTextField textField;
    private ToggleGroup<GuiMarkerInList> typeRadioGroup;
    protected String selectedType = defaultMarker;
    private final List<IMarkerTypeSelectListener> listeners = new ArrayList();
    private final GuiScrollingContainer scroller = new GuiScrollingContainer();

    public interface IMarkerTypeSelectListener {
        void onSelectMarkerType(String str);
    }

    public GuiMarkerFinalizer() {
        this.scroller.setWheelScrollsHorizontally();
        addChild(this.scroller);
    }

    public void setMarkerData(World world, int atlasID, int dimension, int markerX, int markerZ) {
        this.world = world;
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.x = markerX;
        this.z = markerZ;
        setBlocksScreen(true);
    }

    public void addListener(IMarkerTypeSelectListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(IMarkerTypeSelectListener listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public void initGui() {
        List<GuiButton> list = this.buttonList;
        GuiButton guiButton = new GuiButton(0, ((this.width / 2) - 100) - 2, (this.height / 2) + 40, 100, 20, I18n.getString("gui.done"));
        this.btnDone = guiButton;
        list.add(guiButton);
        List<GuiButton> list2 = this.buttonList;
        GuiButton guiButton2 = new GuiButton(0, (this.width / 2) + 2, (this.height / 2) + 40, 100, 20, I18n.getString("gui.cancel"));
        this.btnCancel = guiButton2;
        list2.add(guiButton2);
        this.textField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, (this.width - 200) / 2, (this.height / 2) - 81, 200, 20);
        this.textField.setFocused(true);
        this.textField.setText("");
        this.scroller.removeAllContent();
        int allTypesWidth = (MarkerTextureMap.instance().getAllTypes().size() * 35) - 1;
        int scrollerWidth = Math.min(allTypesWidth, 240);
        this.scroller.setViewportSize(scrollerWidth, 34);
        this.scroller.setGuiCoords((this.width - scrollerWidth) / 2, (this.height / 2) - 25);
        this.typeRadioGroup = new ToggleGroup<>();
        this.typeRadioGroup.addListener(button -> {
            this.selectedType = button.getMarkerType();
            for (IMarkerTypeSelectListener listener : this.listeners) {
                listener.onSelectMarkerType(this.selectedType);
            }
        });
        int contentX = 0;
        for (String markerType : MarkerTextureMap.instance().getAllTypes()) {
            GuiMarkerInList markerGui = new GuiMarkerInList(markerType);
            this.typeRadioGroup.addButton(markerGui);
            if (this.selectedType.equals(markerType)) {
                this.typeRadioGroup.setSelectedButton(markerGui);
            }
            this.scroller.addContent(markerGui).setRelativeX(contentX);
            contentX += 35;
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        this.textField.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);
        this.textField.textboxKeyTyped(par1, par2);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == this.btnDone) {
            AtlasAPI.getMarkerAPI().putMarker(this.world, true, this.atlasID, this.selectedType, this.textField.getText(), this.x, this.z);
            close();
        } else if (button == this.btnCancel) {
            close();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        drawDefaultBackground();
        drawCenteredString("标签:", (this.height / 2) - 97, 16777215, true);
        this.textField.drawTextBox();
        drawCenteredString("类型:", (this.height / 2) - 44, 16777215, true);
        drawGradientRect(this.scroller.getGuiX() - 4, this.scroller.getGuiY() - 4, this.scroller.getGuiX() + this.scroller.getWidth() + 4, this.scroller.getGuiY() + this.scroller.getHeight() + 4, -2012213232, -1727000560);
        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
