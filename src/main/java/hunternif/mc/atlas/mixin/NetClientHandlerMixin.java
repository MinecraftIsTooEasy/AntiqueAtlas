package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.AtlasNetHandler;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.*;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.Minecraft;
import net.minecraft.NetClientHandler;
import net.minecraft.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin implements AtlasNetHandler {
    @Shadow private Minecraft mc;
    @Shadow private WorldClient worldClient;

    @Override
    public void handleMapData(MapDataPacket pkt) {
        if (pkt.data == null) {
            return;
        }
        AtlasData atlasData = AntiqueAtlasItem.itemAtlas.getAtlasData(pkt.atlasID, this.worldClient);
        atlasData.readFromPacket(pkt);
    }

    @Override
    public void handleMapData(PutBiomeTilePacket pkt) {
        AtlasData data = AntiqueAtlasItem.itemAtlas.getAtlasData(pkt.atlasID, this.mc.theWorld);
        data.setTile(pkt.dimension, pkt.x, pkt.z, new Tile(pkt.biomeID));
    }

    @Override
    public void handleMapData(TilesPacket pkt) {
        ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
        for (Map.Entry<ShortVec2, Integer> entry : pkt.biomeMap.entrySet()) {
            ShortVec2 key = entry.getKey();
            data.setBiomeIdAt(pkt.dimension, key.x, key.y, entry.getValue());
        }
    }

    @Override
    public void handleMapData(MarkersPacket pkt) {
        MarkersData markersData;
        if (pkt.isGlobal()) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItem.itemAtlas.getMarkersData(pkt.atlasID, this.mc.theWorld);
        }
        MarkersData markersData2 = markersData;
        for (Marker marker : pkt.markersByType.values()) {
            markersData2.loadMarker(marker);
        }
    }

    @Override
    public void handleMapData(DeleteMarkerPacket pkt) {
        MarkersData markersData;
        if (pkt.isGlobal()) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItem.itemAtlas.getMarkersData(pkt.atlasID, this.mc.theWorld);
        }
        MarkersData data = markersData;
        data.removeMarker(pkt.markerID);
    }

    @Override
    public void handleMapData(TileNameIDPacket pkt) {
        for (Map.Entry<String, Integer> entry : pkt.nameToIdMap.entrySet()) {
            ExtTileIdMap.instance().setPseudoBiomeID(entry.getKey(), entry.getValue());
        }
        TileApiImpl tileAPI = (TileApiImpl) AtlasAPI.getTileAPI();
        tileAPI.onTileIdRegistered(pkt.nameToIdMap);
    }
}
