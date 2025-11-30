package hunternif.mc.atlas.marker;

import net.minecraft.EntityPlayer;
import net.minecraft.World;

public class GlobalMarkersDataHandler {
    private static final String DATA_KEY = "aAtlasGlobalMarkers";
    private GlobalMarkersData data;

    public void onWorldLoad(World world) {
        if (!world.isRemote) {
            this.data = (GlobalMarkersData) world.loadItemData(GlobalMarkersData.class, DATA_KEY);
            if (this.data == null) {
                this.data = new GlobalMarkersData(DATA_KEY);
                this.data.markDirty();
                world.setItemData(DATA_KEY, this.data);
            }
        }
    }

    public GlobalMarkersData getData() {
        if (this.data == null) {
            this.data = new GlobalMarkersData(DATA_KEY);
        }
        return this.data;
    }

    public void onPlayerLogin(EntityPlayer player) {
        this.getData().syncOnPlayer(player);
    }
}
