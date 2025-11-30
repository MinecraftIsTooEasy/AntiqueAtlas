package hunternif.mc.atlas;

import com.google.common.eventbus.Subscribe;
import hunternif.mc.atlas.network.*;
import net.xiaoyu233.fml.reload.event.PacketRegisterEvent;

public class AntiqueAtlasEvent {
    @Subscribe
    public void onPacketRegister(PacketRegisterEvent event) {
        event.register(160, true, false, MapDataPacket.class);
        event.register(161, true, false, TilesPacket.class);
        event.register(162, true, false, MarkersPacket.class);
        event.register(163, true, true, PutBiomeTilePacket.class);
        event.register(164, true, false, TileNameIDPacket.class);
        event.register(165, true, true, DeleteMarkerPacket.class);
        event.register(166, false, true, AddMarkerPacket.class);
        event.register(167, false, true, RegisterTileIdPacket.class);
    }
}
