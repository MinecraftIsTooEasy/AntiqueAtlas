package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.api.AtlasNetHandler;
import hunternif.mc.atlas.network.*;
import net.minecraft.NetHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandler.class)
public class NetHandlerMixin implements AtlasNetHandler {
    public void handleMapData(MapDataPacket pkt) {
    }

    public void handleMapData(RegisterTileIdPacket pkt) {
    }

    public void handleMapData(PutBiomeTilePacket pkt) {
    }

    public void handleMapData(TilesPacket pkt) {
    }

    public void handleMapData(MarkersPacket pkt) {
    }

    public void handleMapData(AddMarkerPacket pkt) {
    }

    public void handleMapData(TileNameIDPacket pkt) {
    }

    public void handleMapData(DeleteMarkerPacket pkt) {
    }
}
