package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AddMarkerPacket;
import hunternif.mc.atlas.network.DeleteMarkerPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.network.AtlasNetwork;
import hunternif.mc.atlas.util.Log;
import net.minecraft.ResourceLocation;
import net.minecraft.World;

public class MarkerApiImpl implements MarkerAPI {
    private static final int GLOBAL = -1;

    @Override
    public void setTexture(String markerType, ResourceLocation texture) {
        MarkerTextureMap.instance().setTexture(markerType, texture);
    }

    @Override
    public void putMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
        doPutMarker(world, visibleAhead, atlasID, markerType, label, x, z);
    }

    @Override
    public void putGlobalMarker(World world, boolean visibleAhead, String markerType, String label, int x, int z) {
        doPutMarker(world, visibleAhead, -1, markerType, label, x, z);
    }

    private void doPutMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
        if (world.isRemote) {
            if (atlasID == -1) {
                Log.warn("Client tried to add a global marker!");
                return;
            } else {
                AtlasNetwork.sendToServer(new AddMarkerPacket(atlasID, world.provider.dimensionId, markerType, label, x, z, visibleAhead));
                return;
            }
        }
        if (atlasID == -1) {
            MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
            Marker marker = data.createAndSaveMarker(markerType, label, world.provider.dimensionId, x, z, visibleAhead);
            AtlasNetwork.sendToAll(new MarkersPacket(world.provider.dimensionId, marker));
        } else {
            MarkersData data2 = AntiqueAtlasItem.itemAtlas.getMarkersData(atlasID, world);
            Marker marker2 = data2.createAndSaveMarker(markerType, label, world.provider.dimensionId, x, z, visibleAhead);
            AtlasNetwork.sendToAll(new MarkersPacket(atlasID, world.provider.dimensionId, marker2));
        }
    }

    @Override
    public void deleteMarker(World world, int atlasID, int markerID) {
        doDeleteMarker(world, atlasID, markerID);
    }

    @Override
    public void deleteGlobalMarker(World world, int markerID) {
        doDeleteMarker(world, -1, markerID);
    }

    private void doDeleteMarker(World world, int atlasID, int markerID) {
        MarkersData markersData;
        DeleteMarkerPacket packet = atlasID == -1 ? new DeleteMarkerPacket(markerID) : new DeleteMarkerPacket(atlasID, markerID);
        if (world.isRemote) {
            if (atlasID == -1) {
                Log.warn("Client tried to delete a global marker!");
                return;
            } else {
                AtlasNetwork.sendToServer(packet);
                return;
            }
        }
        if (atlasID == -1) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItem.itemAtlas.getMarkersData(atlasID, world);
        }
        MarkersData data = markersData;
        data.removeMarker(markerID);
        AtlasNetwork.sendToAll(packet);
    }
}
