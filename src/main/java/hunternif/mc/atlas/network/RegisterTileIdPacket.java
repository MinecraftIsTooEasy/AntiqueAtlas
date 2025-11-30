package hunternif.mc.atlas.network;

import hunternif.mc.atlas.api.AtlasNetHandler;
import net.minecraft.NetHandler;
import net.minecraft.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RegisterTileIdPacket extends Packet {
    public String name;

    public RegisterTileIdPacket(String uniqueTileName) {
        this.name = uniqueTileName;
    }

    @Override
    public void readPacketData(DataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler) handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        return getPacketSizeOfString(this.name);
    }
}
