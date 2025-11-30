package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.AtlasNetHandler;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.*;
import hunternif.mc.atlas.util.Log;
import net.minecraft.ItemStack;
import net.minecraft.NetServerHandler;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetServerHandler.class)
public class NetServerHandlerMixin implements AtlasNetHandler {
    @Shadow public ServerPlayer playerEntity;

    @Override
    public void handleMapData(AddMarkerPacket pkt) {
        if (!this.playerEntity.inventory.hasItemStack(new ItemStack(AntiqueAtlasItem.itemAtlas, 1, pkt.atlasID))) {
            Log.error("Player %s attempted to put marker into someone else's Atlas #%d", this.playerEntity.getEntityName(), pkt.atlasID);
        }
        MarkersData markersData = AntiqueAtlasItem.itemAtlas.getMarkersData(pkt.atlasID, this.playerEntity.worldObj);
        Marker marker = markersData.createAndSaveMarker(pkt.type, pkt.label, pkt.dimension, pkt.x, pkt.y, pkt.visibleAhead);
        MarkersPacket packetForClients = new MarkersPacket(pkt.atlasID, pkt.dimension, marker);
        AtlasNetwork.sendToAll(packetForClients);
    }

    @Override
    public void handleMapData(RegisterTileIdPacket pkt) {
        int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(pkt.name);
        TileNameIDPacket packet = new TileNameIDPacket();
        packet.put(pkt.name, biomeID);
        AtlasNetwork.sendToAll(packet);
    }

    @Override
    public void handleMapData(PutBiomeTilePacket pkt) {
        if (!this.playerEntity.inventory.hasItemStack(new ItemStack(AntiqueAtlasItem.itemAtlas, 1, pkt.atlasID))) {
            Log.error("Player %s attempted to modify someone else's Atlas #%d", this.playerEntity.getEntityName(), pkt.atlasID);
        } else {
            AtlasAPI.getTileAPI().putBiomeTile(this.playerEntity.worldObj, pkt.atlasID, pkt.biomeID, pkt.x, pkt.z);
        }
    }

    @Override
    public void handleMapData(DeleteMarkerPacket pkt) {
        if (!this.playerEntity.inventory.hasItemStack(new ItemStack(AntiqueAtlasItem.itemAtlas, 1, pkt.atlasID))) {
            Log.error("Player %s attempted to delete marker from someone else's Atlas #%d", this.playerEntity.getEntityName(), pkt.atlasID);
        } else if (pkt.isGlobal()) {
            AtlasAPI.getMarkerAPI().deleteGlobalMarker(this.playerEntity.worldObj, pkt.markerID);
        } else {
            AtlasAPI.getMarkerAPI().deleteMarker(this.playerEntity.worldObj, pkt.atlasID, pkt.markerID);
        }
    }
}
