package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.core.ITileStorage;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.*;

public class ItemAtlas extends ItemMap {
    public static final String ATLAS_DATA_PREFIX = "aAtlas_";
    public static final String WORLD_ATLAS_DATA_ID = "aAtlas";
    public static final String MARKERS_DATA_PREFIX = "aaMarkers_";
    public static double LOOK_RADIUS = 11.0d;
    public static int UPDATE_INTERVAL = 20;

    public ItemAtlas(int id) {
        super(id, "antiqueAtlas");
        setUnlocalizedName("antiqueAtlas");
    }

    @Override
    public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
        if (player.worldObj.isRemote) {
            AntiqueAtlasMod.openAtlasGUI(player.getHeldItemStack());
            return true;
        }
        return true;
    }

    @Override // net.minecraft.item.ItemMap, net.minecraft.item.Item
    public void onUpdate(ItemStack stack, World world, Entity entity, int idx, boolean isCurrent) {
        int biomeId;
        AtlasData data = getAtlasData(stack, world);
        if (data == null || !(entity instanceof EntityPlayer player)) {
            return;
        }
        if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
            data.syncOnPlayer(stack.getItemSubtype(), player);
        }
        MarkersData markers = getMarkersData(stack, world);
        if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
            markers.syncOnPlayer(stack.getItemSubtype(), player);
        }
        ChunkBiomeAnalyzer biomeAnalyzer = ChunkBiomeAnalyzer.instance;
        if (player.ticksExisted % UPDATE_INTERVAL != 0 || biomeAnalyzer == null) {
            return;
        }
        int playerX = MathHelper.floor_double(player.posX) >> 4;
        int playerZ = MathHelper.floor_double(player.posZ) >> 4;
        ITileStorage seenChunks = data.getDimensionData(player.dimension);

        for (double dx = -LOOK_RADIUS; dx <= LOOK_RADIUS; dx += 1.0d) {
            for (double dz = -LOOK_RADIUS; dz <= LOOK_RADIUS; dz += 1.0d) {
                if ((dx * dx) + (dz * dz) <= LOOK_RADIUS * LOOK_RADIUS) {
                    int x = (int) (playerX + dx);
                    int y = (int) (playerZ + dz);
                    int biomeId2 = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(player.dimension, x, y);
                    if (biomeId2 == -1) {
                        if (!seenChunks.hasTileAt(x, y) && player.worldObj.blockExists(x << 4, 0, y << 4)) {
                            Chunk chunk = player.worldObj.getChunkFromChunkCoords(x, y);
                            if (!chunk.isChunkLoaded) {
                                biomeId = -1;
                            } else {
                                biomeId = biomeAnalyzer.getMeanBiomeID(chunk);
                            }
                            if (biomeId != -1) {
                                data.setTile(player.dimension, x, y, new Tile(biomeId));
                            }
                        }
                    } else {
                        Tile tile = new Tile(biomeId2);
                        if (world.isRemote) {
                            tile.randomizeTexture();
                        }
                        data.setTile(player.dimension, x, y, tile);
                    }
                }
            }
        }
    }

    public AtlasData getAtlasData(ItemStack stack, World world) {
        if (stack.getItem() == AntiqueAtlasItem.itemAtlas) {
            return getAtlasData(stack.getItemSubtype(), world);
        }
        return null;
    }

    public AtlasData getAtlasData(int atlasID, World world) {
        String key = getAtlasDataKey(atlasID);
        AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);
        if (data == null) {
            data = new AtlasData(key);
            world.setItemData(key, data);
        }
        return data;
    }

    public String getAtlasDataKey(int atlasID) {
        return ATLAS_DATA_PREFIX + atlasID;
    }

    public MarkersData getMarkersData(ItemStack stack, World world) {
        if (stack.getItem() == AntiqueAtlasItem.itemAtlas) {
            return getMarkersData(stack.getItemSubtype(), world);
        }
        return null;
    }

    public MarkersData getMarkersData(int atlasID, World world) {
        String key = getMarkersDataKey(atlasID);
        MarkersData data = (MarkersData) world.loadItemData(MarkersData.class, key);
        if (data == null) {
            data = new MarkersData(key);
            world.setItemData(key, data);
        }
        return data;
    }

    public String getMarkersDataKey(int atlasID) {
        return MARKERS_DATA_PREFIX + atlasID;
    }
}
