package hunternif.mc.atlas.network;

import net.minecraft.Minecraft;
import net.minecraft.Packet;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class AtlasNetwork {
//    public static final ResourceLocation OpenWindow = new ResourceLocation(ShopInit.ShopModID, "OpenWindow");
//    public static final ResourceLocation OpenShop = new ResourceLocation(ShopInit.ShopModID, "OpenShop");
//    public static final ResourceLocation SyncShopInfo = new ResourceLocation(ShopInit.ShopModID, "SyncShopInfo");
//    public static final ResourceLocation SyncMoney = new ResourceLocation(ShopInit.ShopModID, "SyncMoney");
//    public static final ResourceLocation SyncPrice = new ResourceLocation(ShopInit.ShopModID, "SyncPrice");
//    public static final ResourceLocation ContainerButtonClick = new ResourceLocation(ShopInit.ShopModID, "ContainerButtonClick");
//
//    public static void sendToClient(ServerPlayer player, Packet packet) {
//        Network.sendToClient(player, packet);
//    }
//
//    public static void sendToServer(Packet packet) {
//        Network.sendToServer(packet);
//    }
//
//    public static void init() {
//        if (!FishModLoader.isServer()) {
//            initClient();
//        }
//        initServer();
//    }
//
//    private static void initClient() {
//        PacketReader.registerClientPacketReader(ShopNetwork.OpenWindow, S2COpenWindow::new);
//        PacketReader.registerClientPacketReader(ShopNetwork.SyncShopInfo, S2CSyncShopInfo::new);
//        PacketReader.registerClientPacketReader(ShopNetwork.SyncMoney, S2CSyncMoney::new);
//        PacketReader.registerClientPacketReader(ShopNetwork.SyncPrice, S2CSyncPrice::new);
//    }
//
//    private static void initServer() {
//        PacketReader.registerServerPacketReader(ShopNetwork.OpenShop, packetByteBuf -> new C2SOpenShop());
//        PacketReader.registerServerPacketReader(ShopNetwork.ContainerButtonClick, C2SContainerButtonClick::new);
//    }
    public static void sendTo(Packet packet, ServerPlayer player) {
        player.sendPacket(packet);
    }

    public static void sendToAll(Packet packet) {
        MinecraftServer server = MinecraftServer.getServer();
        server.getConfigurationManager().sendPacketToAllPlayers(packet);
    }

    public static void sendToServer(Packet packet) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendPacket(packet);
    }
}
