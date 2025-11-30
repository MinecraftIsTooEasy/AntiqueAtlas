package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.GuiMarkerFinalizer;
import net.minecraft.MaterialName;
import net.minecraft.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum MarkerTextureMap {
    INSTANCE;

    private final Map<String, ResourceLocation> map = new HashMap<>();
    private final ResourceLocation defaultTexture = Textures.MARKER_RED_X_LARGE;

    public static MarkerTextureMap instance() {
        return INSTANCE;
    }

    MarkerTextureMap() {
        registerDefaultMarker();
    }

    private void registerDefaultMarker() {
        setTexture("bed", Textures.MARKER_BED);
        setTexture(MaterialName.diamond, Textures.MARKER_DIAMOND);
        setTexture("google", Textures.MARKER_GOOGLE_MARKER);
        setTexture("nether_portal", Textures.MARKER_NETHER_PORTAL);
        setTexture("pickaxe", Textures.MARKER_PICKAXE);
        setTexture("red_x_large", Textures.MARKER_RED_X_LARGE);
        setTexture(GuiMarkerFinalizer.defaultMarker, Textures.MARKER_RED_X_SMALL);
        setTexture("scroll", Textures.MARKER_SCROLL);
        setTexture("skull", Textures.MARKER_SKULL);
        setTexture("sword", Textures.MARKER_SWORD);
        setTexture("tomb", Textures.MARKER_TOMB);
        setTexture("tower", Textures.MARKER_TOWER);
//        setTexture("unknown", Textures.MARKER_UNKNOWN_MARKER);
        setTexture("village", Textures.MARKER_VILLAGE);
    }

    public void setTexture(String markerType, ResourceLocation texture) {
        this.map.put(markerType, texture);
    }

    public boolean setTextureIfNone(String markerType, ResourceLocation texture) {
        if (this.map.containsKey(markerType)) {
            return false;
        }
        this.map.put(markerType, texture);
        return true;
    }

    public ResourceLocation getTexture(String markerType) {
        ResourceLocation texture = this.map.get(markerType);
        return texture == null ? this.defaultTexture : texture;
    }

    Map<String, ResourceLocation> getMap() {
        return this.map;
    }

    public Collection<String> getAllTypes() {
        return this.map.keySet();
    }

    public Collection<ResourceLocation> getAllTextures() {
        return this.map.values();
    }
}
