package hunternif.mc.atlas.core;

import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.*;

import java.util.*;

public class AtlasData extends WorldSavedData {
    private static final int VERSION = 1;
    private static final String TAG_VERSION = "aaVersion";
    private static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
    private static final String TAG_DIMENSION_ID = "qDimensionID";
    private static final String TAG_VISITED_CHUNKS = "qVisitedChunks";
    private final Map<Integer, DimensionData> dimensionMap;
    private final Set<NetServerHandler> playersSentTo;
    private final NBTTagCompound nbtCache;
    private byte[] rawData;

    public AtlasData(String key) {
        super(key);
        this.dimensionMap = new HashMap<>();
        this.playersSentTo = new HashSet<>();
        this.nbtCache = new NBTTagCompound();
    }

    public void readFromPacket(MapDataPacket pkt) {
        NBTTagCompound nbt = CompressedStreamTools.decompress(pkt.data);
        readFromNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        int version = compound.getInteger(TAG_VERSION);
        if (version < 1) {
            Log.error("Outdated atlas data format! Was %d but current is %d", version, 1);
            markDirty();
        }
        NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST);
        for (int d = 0; d < dimensionMapList.tagCount(); d++) {
            NBTTagCompound tag = (NBTTagCompound) dimensionMapList.tagAt(d);
            int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
            int[] intArray = tag.getIntArray(TAG_VISITED_CHUNKS);
            for (int i = 0; i < intArray.length; i += 3) {
                setTile(dimensionID, intArray[i], intArray[i + 1], new Tile(intArray[i + 2]));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nBTTagCompound) {
        nBTTagCompound.setInteger(TAG_VERSION, 1);
        NBTTagList dimensionMapList = new NBTTagList();
        for (Map.Entry<Integer, DimensionData> dimensionEntry : this.dimensionMap.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(TAG_DIMENSION_ID, dimensionEntry.getKey());
            Map<ShortVec2, Tile> seenChunks = dimensionEntry.getValue().getSeenChunks();
            int[] intArray = new int[seenChunks.size() * 3];
            int i = 0;
            for (Map.Entry<ShortVec2, Tile> entry : seenChunks.entrySet()) {
                int i2 = i;
                int i3 = i + 1;
                intArray[i2] = entry.getKey().x;
                int i4 = i3 + 1;
                intArray[i3] = entry.getKey().y;
                i = i4 + 1;
                intArray[i4] = entry.getValue().biomeID;
            }
            tag.setIntArray(TAG_VISITED_CHUNKS, intArray);
            dimensionMapList.appendTag(tag);
        }
        nBTTagCompound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
    }

    public void setTile(int dimension, int x, int y, Tile tile) {
        DimensionData dimData = getDimensionData(dimension);
        dimData.setTile(x, y, tile);
        markDirty();
        this.rawData = null;
    }

    public Set<Integer> getVisitedDimensions() {
        return this.dimensionMap.keySet();
    }

    public DimensionData getDimensionData(int dimension) {
        DimensionData dimData = this.dimensionMap.get(dimension);
        if (dimData == null) {
            dimData = new DimensionData(dimension);
            this.dimensionMap.put(dimension, dimData);
        }
        return dimData;
    }

    public Map<ShortVec2, Tile> getSeenChunksInDimension(int dimension) {
        return getDimensionData(dimension).getSeenChunks();
    }

    public boolean isSyncedOnPlayer(EntityPlayer player) {
        if (!player.onServer()) {
            Log.warn("client data can't sync");
            return true;
        }
        ServerPlayer playerMP = (ServerPlayer) player;
        return this.playersSentTo.contains(playerMP.playerNetServerHandler);
    }

    public void syncOnPlayer(int atlasID, EntityPlayer player) {
        if (!player.onServer()) {
            Log.warn("client data can't sync");
            return;
        }
        ServerPlayer playerMP = (ServerPlayer) player;
        playerMP.sendPacket(new MapDataPacket(atlasID, getOrUpdateRawData()));
        this.playersSentTo.removeIf(NetServerHandler::isConnectionClosed);
        this.playersSentTo.add(playerMP.playerNetServerHandler);
    }

    public void sendPacketToSyncPlayer(Packet packet) {
        Iterator<NetServerHandler> it = this.playersSentTo.iterator();
        while (it.hasNext()) {
            NetServerHandler next = it.next();
            if (next.isConnectionClosed()) {
                it.remove();
            } else {
                next.netManager.addToSendQueue(packet);
            }
        }
    }

    private byte[] getOrUpdateRawData() {
        if (this.rawData != null) {
            return this.rawData;
        }
        NBTTagCompound.getTagMap(this.nbtCache).clear();
        writeToNBT(this.nbtCache);
        this.rawData = CompressedStreamTools.compress(this.nbtCache);
        if (this.rawData.length >= 65535) {
            Log.error("atlas data len=%d", this.rawData.length);
        }
        return this.rawData;
    }

    public boolean isEmpty() {
        return this.dimensionMap.isEmpty();
    }
}
