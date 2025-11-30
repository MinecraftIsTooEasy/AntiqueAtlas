package hunternif.mc.atlas;

import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtBiomeDataHandler;
import hunternif.mc.atlas.ext.VillageWatcher;
import hunternif.mc.atlas.marker.GlobalMarkersDataHandler;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.event.listener.IWorldLoadListener;
import net.fabricmc.api.ModInitializer;
import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.ModResourceManager;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class AntiqueAtlasMod implements ModInitializer {
    public static final String ID = "antiqueatlas";
    public static final String NAME = "Antique Atlas";
    public static final String CHANNEL = ID;
    public static final String VERSION = "@VERSION@";
    public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
    public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
    private static final VillageWatcher villageWatcher = new VillageWatcher();
    public static final SettingsConfig settings = new SettingsConfig();

    public static void onPopulateChunkPost(World world) {
        villageWatcher.onPopulateChunkPost(world);
    }

    public static void openAtlasGUI(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) {
            GuiAtlas guiAtlas = new GuiAtlas();
            mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
        }
    }

    public static void updateBiomeTextureConfig() {
    }

    public static void updateMarkerTextureConfig() {
    }

    public static void updateExtTileConfig() {
    }

    @Override
    public void onInitialize() {
        settings.load(new File(FishModLoader.CONFIG_DIR, "settings.cfg"));
        MITEEvents.MITE_EVENT_BUS.register(new AntiqueAtlasEvent());
        ModResourceManager.addResourcePackDomain("antiqueatlas");
        Handlers.WorldLoad.register(new IWorldLoadListener() {
            @Override
            public void onWorldLoad(WorldClient world) {
                extBiomeData.onWorldLoad(world);
                globalMarkersData.onWorldLoad(world);
                villageWatcher.onWorldLoad(world);
            }
        });
        Handlers.PlayerEvent.register(new IPlayerEventListener() {
            @Override
            public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
                extBiomeData.onPlayerLogin(event.player());
                globalMarkersData.onPlayerLogin(event.player());
            }
        });
        Handlers.Crafting.register(
                registerEvent -> registerEvent.registerShapelessRecipe(
                        new ItemStack(AntiqueAtlasItem.emptyAtlas, 1),
                        true,
                        Item.book, Item.compass));
    }
}
