package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.TextureSet;
import net.minecraft.BiomeGenBase;
import net.minecraft.ResourceLocation;
import net.minecraft.World;

public interface TileAPI {
    public static final int VERSION = 4;

    TextureSet registerTextureSet(String str, ResourceLocation... resourceLocationArr);

    void setBiomeTexture(int i, String str, ResourceLocation... resourceLocationArr);

    void setBiomeTexture(BiomeGenBase biomeGenBase, String str, ResourceLocation... resourceLocationArr);

    void setBiomeTexture(int i, TextureSet textureSet);

    void setBiomeTexture(BiomeGenBase biomeGenBase, TextureSet textureSet);

    void setCustomTileTexture(String str, ResourceLocation... resourceLocationArr);

    void setCustomTileTexture(String str, TextureSet textureSet);

    void putBiomeTile(World world, int i, int i2, int i3, int i4);

    void putBiomeTile(World world, int i, BiomeGenBase biomeGenBase, int i2, int i3);

    void putCustomTile(World world, int i, String str, int i2, int i3);

    void putCustomGlobalTile(World world, String str, int i, int i2);
}
