package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.SaveData;
import net.minecraft.BiomeGenBase;
import net.minecraft.MathHelper;
import net.minecraft.ResourceLocation;

import java.util.*;

public class BiomeTextureMap extends SaveData {
    final Map<Integer, TextureSet> textureMap = new HashMap<>();
    private static final BiomeTextureMap INSTANCE = new BiomeTextureMap();
    public static final TextureSet defaultTexture = TextureSet.PLAINS;

    public static BiomeTextureMap instance() {
        return INSTANCE;
    }

    public void setTexture(int biomeID, TextureSet textureSet) {
        if (textureSet == null) {
            Log.warn("Texture set is null!");
            return;
        }
        TextureSet previous = this.textureMap.put(biomeID, textureSet);
        if (previous == null) {
            markDirty();
        } else if (!previous.equals(textureSet)) {
            Log.error("Overwriting texture set for biome %d\n", biomeID);
            markDirty();
        }
        this.textureMap.put(biomeID, textureSet);
    }

public void autoRegister(int biomeID) {
    if (biomeID < 0 || biomeID >= 256) {
        Log.error("Biome ID %d is out of range. Auto-registering default texture set", biomeID);
        setTexture(biomeID, defaultTexture);
        return;
    }
    BiomeGenBase biome = BiomeGenBase.biomeList[biomeID];
    if (biome == null) {
        Log.error("Biome ID %d is null. Auto-registering default texture set", biomeID);
        setTexture(biomeID, defaultTexture);
        return;
    }

    if (biome.equals(BiomeGenBase.swampland)) {
        setTexture(biomeID, TextureSet.SWAMP);
    } else if (biome.equals(BiomeGenBase.river) || biome.equals(BiomeGenBase.ocean)) {
        setTexture(biomeID, TextureSet.WATER);
    } else if (biome.equals(BiomeGenBase.beach)) {
        setTexture(biomeID, TextureSet.SHORE);
    } else if (biome.equals(BiomeGenBase.jungle) || biome.equals(BiomeGenBase.jungleHills)) {
        setTexture(biomeID, TextureSet.JUNGLE);
    } else if (biome.equals(BiomeGenBase.forest) || biome.equals(BiomeGenBase.forestHills)) {
        setTexture(biomeID, TextureSet.DENSE_FOREST);
    } else if (biome.equals(BiomeGenBase.plains) || biome.equals(BiomeGenBase.desert)) {
        setTexture(biomeID, TextureSet.PLAINS);
    } else if (biome.equals(BiomeGenBase.extremeHills) || biome.equals(BiomeGenBase.iceMountains)) {
        setTexture(biomeID, TextureSet.MOUNTAINS_NAKED);
    } else {
        setTexture(biomeID, defaultTexture);
    }
    Log.info("Auto-registered standard texture set for biome %d", biomeID);
}

    public void checkRegistration(int biomeID) {
        if (!isRegistered(biomeID)) {
            autoRegister(biomeID);
            markDirty();
        }
    }

    public boolean isRegistered(int biomeID) {
        return this.textureMap.containsKey(biomeID);
    }

    public int getVariations(int biomeID) {
        checkRegistration(biomeID);
        TextureSet set = this.textureMap.get(biomeID);
        return set.textures.length;
    }

    /** If unknown biome, auto-registers a texture set. If null, returns default set. */
    public TextureSet getTextureSet(Tile tile) {
        if (tile == null) return defaultTexture;
        checkRegistration(tile.biomeID);
        return textureMap.get(tile.biomeID);
    }

    public ResourceLocation getTexture(Tile tile) {
        checkRegistration(tile.biomeID);
        TextureSet set = this.textureMap.get(tile.biomeID);
        int i = MathHelper.floor_float((tile.getVariationNumber() / 32767.0f) * set.textures.length);
        return set.textures[i];
    }

//    public boolean shouldStitchTo(int biomeID, int toBiomeID) {
//        checkRegistration(biomeID);
//        checkRegistration(toBiomeID);
//        TextureSet entry = this.textureMap.get(biomeID);
//        TextureSet toEntry = this.textureMap.get(toBiomeID);
//        return entry.shouldStichTo(toEntry);
//    }

    public List<ResourceLocation> getAllTextures() {
        List<ResourceLocation> list = new ArrayList<>(this.textureMap.size());
        for (Map.Entry<Integer, TextureSet> entry : this.textureMap.entrySet()) {
            list.addAll(Arrays.asList(entry.getValue().textures));
        }
        return list;
    }
}
