package hunternif.mc.atlas.api;

import net.minecraft.ResourceLocation;
import net.minecraft.World;

public interface MarkerAPI {
    public static final int VERSION = 3;

    void setTexture(String str, ResourceLocation resourceLocation);

    void putMarker(World world, boolean z, int i, String str, String str2, int i2, int i3);

    void putGlobalMarker(World world, boolean z, String str, String str2, int i, int i2);

    void deleteMarker(World world, int i, int i2);

    void deleteGlobalMarker(World world, int i);
}
