package hunternif.mc.atlas.ext;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import hunternif.mc.atlas.network.AtlasNetwork;
import hunternif.mc.atlas.network.TileNameIDPacket;
import hunternif.mc.atlas.util.SaveData;
import net.minecraft.EntityPlayer;
import net.minecraft.ServerPlayer;

import java.util.Map;

public class ExtTileIdMap extends SaveData {
    private static final ExtTileIdMap INSTANCE = new ExtTileIdMap();
    public static final String TILE_VILLAGE_HOUSE = "npcVillageDoor";
    public static final String TILE_VILLAGE_TERRITORY = "npcVillageTerritory";
    public static final int NOT_FOUND = -1;
    private int lastID = -1;
    private final BiMap<String, Integer> nameToIdMap = HashBiMap.create();

    public static ExtTileIdMap instance() {
        return INSTANCE;
    }

    public int getOrCreatePseudoBiomeID(String uniqueName) {
        Integer id = this.nameToIdMap.get(uniqueName);
        if (id == null) {
            id = findNewID();
            this.nameToIdMap.put(uniqueName, id);
            markDirty();
        }
        return id;
    }

    public int getPseudoBiomeID(String uniqueName) {
        Integer id = this.nameToIdMap.get(uniqueName);
        if (id == null) {
            return -1;
        }
        return id;
    }

    private int findNewID() {
        while (this.lastID > -32768) {
            BiMap<Integer, String> inverse = this.nameToIdMap.inverse();
            int i = this.lastID - 1;
            this.lastID = i;
            if (!inverse.containsKey(i)) {
                break;
            }
        }
        return this.lastID;
    }

    public void setPseudoBiomeID(String uniqueName, int id) {
        this.nameToIdMap.forcePut(uniqueName, id);
    }

    Map<String, Integer> getMap() {
        return this.nameToIdMap;
    }

    public void syncOnPlayer(EntityPlayer player) {
        AtlasNetwork.sendTo(new TileNameIDPacket(this.nameToIdMap), (ServerPlayer) player);
    }
}
