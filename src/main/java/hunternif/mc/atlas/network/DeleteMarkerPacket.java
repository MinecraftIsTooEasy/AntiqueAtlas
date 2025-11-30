package hunternif.mc.atlas.network;

import hunternif.mc.atlas.api.AtlasNetHandler;
import net.minecraft.NetHandler;
import net.minecraft.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DeleteMarkerPacket extends Packet {
    private static final int GLOBAL = -1;
    public int atlasID;
    public int markerID;

    public DeleteMarkerPacket(int atlasID, int markerID) {
        this.atlasID = atlasID;
        this.markerID = markerID;
    }

    public DeleteMarkerPacket(int markerID) {
        this(-1, markerID);
    }

    @Override
    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readUnsignedShort();
        this.markerID = in.readInt();
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeInt(this.markerID);
    }

    public boolean isGlobal() {
        return this.atlasID == -1;
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler) handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        return 6;
    }
}
