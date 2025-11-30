package hunternif.mc.atlas.api;

import hunternif.mc.atlas.network.*;

public interface AtlasNetHandler {
    default void handleMapData(MapDataPacket pkt) {
    }

    default void handleMapData(RegisterTileIdPacket pkt) {
    }

    default void handleMapData(PutBiomeTilePacket pkt) {
    }

    default void handleMapData(TilesPacket pkt) {
    }

    default void handleMapData(MarkersPacket pkt) {
    }

    default void handleMapData(AddMarkerPacket pkt) {
    }

    default void handleMapData(TileNameIDPacket pkt) {
    }

    default void handleMapData(DeleteMarkerPacket pkt) {
    }
}
