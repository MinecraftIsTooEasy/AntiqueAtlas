package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.*;
import hunternif.mc.atlas.util.Log;
import net.minecraft.BiomeGenBase;
import net.minecraft.ResourceLocation;
import net.minecraft.World;

import java.util.HashMap;
import java.util.Map;

public class TileApiImpl implements TileAPI {
    private final Map<String, TextureSet> pendingTextures = new HashMap();
    private final Map<String, TileData> pendingTiles = new HashMap();

    private static class TileData {
        World world;
        int atlasID;
        int x;
        int z;

        TileData(World world, int atlasID, int x, int z) {
            this.world = world;
            this.atlasID = atlasID;
            this.x = x;
            this.z = z;
        }
    }

    @Override
    public TextureSet registerTextureSet(String name, ResourceLocation... textures) {
        TextureSet textureSet = new TextureSet(name, textures);
        TextureSetMap.instance().register(textureSet);
        return textureSet;
    }

    @Override
    public void setBiomeTexture(int biomeID, String textureSetName, ResourceLocation... textures) {
        TextureSet textureSet = new TextureSet(textureSetName, textures);
        TextureSetMap.instance().register(textureSet);
        setBiomeTexture(biomeID, textureSet);
    }

    @Override
    public void setBiomeTexture(BiomeGenBase biome, String textureSetName, ResourceLocation... textures) {
        setBiomeTexture(biome.biomeID, textureSetName, textures);
    }

    @Override
    public void setBiomeTexture(int biomeID, TextureSet textureSet) {
        BiomeTextureMap.instance().setTexture(biomeID, textureSet);
    }

    @Override
    public void setBiomeTexture(BiomeGenBase biome, TextureSet textureSet) {
        setBiomeTexture(biome.biomeID, textureSet);
    }

    @Override
    public void setCustomTileTexture(String uniqueTileName, ResourceLocation... textures) {
        TextureSet set = new TextureSet(uniqueTileName, textures);
        TextureSetMap.instance().register(set);
        setCustomTileTexture(uniqueTileName, set);
    }

    @Override
    public void setCustomTileTexture(String uniqueTileName, TextureSet textureSet) {
        int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
        if (id != -1) {
            BiomeTextureMap.instance().setTexture(id, textureSet);
        } else {
            this.pendingTextures.put(uniqueTileName, textureSet);
            AtlasNetwork.sendToServer(new RegisterTileIdPacket(uniqueTileName));
        }
    }

    @Override
    public void putBiomeTile(World world, int atlasID, int biomeID, int chunkX, int chunkZ) {
        int dimension = world.provider.dimensionId;
        PutBiomeTilePacket packet = new PutBiomeTilePacket(atlasID, dimension, chunkX, chunkZ, biomeID);
        if (world.isRemote) {
            AtlasNetwork.sendToServer(packet);
            return;
        }
        AtlasData data = AntiqueAtlasItem.itemAtlas.getAtlasData(atlasID, world);
        Tile tile = new Tile(biomeID);
        data.setTile(dimension, chunkX, chunkZ, tile);
        data.sendPacketToSyncPlayer(packet);
    }

    @Override
    public void putBiomeTile(World world, int atlasID, BiomeGenBase biome, int chunkX, int chunkZ) {
        putBiomeTile(world, atlasID, biome.biomeID, chunkX, chunkZ);
    }

    @Override
    public void putCustomTile(World world, int atlasID, String tileName, int chunkX, int chunkZ) {
        if (world.isRemote) {
            int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
            if (biomeID != -1) {
                putBiomeTile(world, atlasID, biomeID, chunkX, chunkZ);
                return;
            } else {
                this.pendingTiles.put(tileName, new TileData(world, atlasID, chunkX, chunkZ));
                AtlasNetwork.sendToServer(new RegisterTileIdPacket(tileName));
                return;
            }
        }
        int biomeID2 = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
        if (biomeID2 == -1) {
            biomeID2 = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
            TileNameIDPacket packet = new TileNameIDPacket();
            packet.put(tileName, biomeID2);
            AtlasNetwork.sendToAll(packet);
        }
        putBiomeTile(world, atlasID, biomeID2, chunkX, chunkZ);
    }

    @Override
    public void putCustomGlobalTile(World world, String tileName, int chunkX, int chunkZ) {
        if (world.isRemote) {
            Log.warn("Client tried to put global tile");
            return;
        }
        boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != -1;
        int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
        ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
        data.setBiomeIdAt(world.provider.dimensionId, chunkX, chunkZ, biomeID);
        if (!isIdRegistered) {
            TileNameIDPacket packet = new TileNameIDPacket();
            packet.put(tileName, biomeID);
            AtlasNetwork.sendToAll(packet);
        }
        TilesPacket packet2 = new TilesPacket(world.provider.dimensionId);
        packet2.addTile(chunkX, chunkZ, biomeID);
        AtlasNetwork.sendToAll(packet2);
    }

    public void onTileIdRegistered(Map<String, Integer> nameToIdMap) {
        for (Map.Entry<String, Integer> entry : nameToIdMap.entrySet()) {
            TextureSet texture = this.pendingTextures.remove(entry.getKey());
            if (texture != null) {
                BiomeTextureMap.instance().setTexture(entry.getValue(), texture);
            }
            TileData tile = this.pendingTiles.remove(entry.getKey());
            if (tile != null) {
                putBiomeTile(tile.world, tile.atlasID, entry.getValue(), tile.x, tile.z);
            }
        }
    }
}
