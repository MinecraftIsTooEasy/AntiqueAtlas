package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.network.MarkersPacket;
import net.minecraft.EntityPlayer;

public class GlobalMarkersData extends MarkersData {
    public GlobalMarkersData(String key) {
        super(key);
    }

    @Override
    public Marker createAndSaveMarker(String type, String label, int dimension, int x, int y, boolean visibleAhead) {
        return super.createAndSaveMarker(type, label, dimension, x, y, visibleAhead).setGlobal(true);
    }

    @Override
    public Marker loadMarker(Marker marker) {
        return super.loadMarker(marker).setGlobal(true);
    }

    protected void syncOnPlayer(EntityPlayer player) {
        syncOnPlayer(-1, player);
    }

    @Override
    protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
        return new MarkersPacket(dimension);
    }
}
