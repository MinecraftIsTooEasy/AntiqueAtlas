package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.gui.core.GuiBlinkingImage;
import hunternif.mc.atlas.marker.MarkerTextureMap;

public class GuiBlinkingMarker extends GuiBlinkingImage implements GuiMarkerFinalizer.IMarkerTypeSelectListener {
    @Override
    public void onSelectMarkerType(String markerType) {
        setTexture(MarkerTextureMap.instance().getTexture(markerType), 32, 32);
    }
}
