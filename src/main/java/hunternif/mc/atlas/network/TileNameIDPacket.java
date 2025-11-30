package hunternif.mc.atlas.network;

import hunternif.mc.atlas.api.AtlasNetHandler;
import net.minecraft.NetHandler;
import net.minecraft.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TileNameIDPacket extends Packet {
    public Map<String, Integer> nameToIdMap;

    public TileNameIDPacket() {
    }

    public TileNameIDPacket(Map<String, Integer> nameToIdMap) {
        this.nameToIdMap = nameToIdMap;
    }

    public TileNameIDPacket put(String name, int biomeID) {
        if (this.nameToIdMap == null) {
            this.nameToIdMap = new HashMap<>();
        }
        this.nameToIdMap.put(name, biomeID);
        return this;
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.nameToIdMap.size());
        for (Map.Entry<String, Integer> entry : this.nameToIdMap.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeShort(entry.getValue());
        }
    }

    @Override
    public void readPacketData(DataInput in) throws IOException {
        int length = in.readShort();
        this.nameToIdMap = new HashMap<>();
        for (int i = 0; i < length; i++) {
            this.nameToIdMap.put(in.readUTF(), (int) in.readShort());
        }
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler) handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        int ret = 2;
        for (Map.Entry<String, Integer> entry : this.nameToIdMap.entrySet()) {
            ret = ret + getPacketSizeOfString(entry.getKey()) + 2;
        }
        return ret;
    }
}
