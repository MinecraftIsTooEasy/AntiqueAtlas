package hunternif.mc.atlas.network;

import hunternif.mc.atlas.api.AtlasNetHandler;
import net.minecraft.NetHandler;
import net.minecraft.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MapDataPacket extends Packet {
    public int atlasID;
    public byte[] data;

    public MapDataPacket(int atlasID, byte[] data) {
        this.atlasID = atlasID;
        this.data = data;
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeInt(this.data.length);
        out.write(this.data);
    }

    @Override
    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readUnsignedShort();
        int len = in.readInt();
        this.data = new byte[len];
        in.readFully(this.data);
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler) handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        return 6 + this.data.length;
    }
}
