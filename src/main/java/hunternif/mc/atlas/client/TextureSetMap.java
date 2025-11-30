package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.SaveData;
import net.minecraft.ResourceLocation;

import java.util.*;

public class TextureSetMap extends SaveData {
    private static final TextureSetMap INSTANCE = new TextureSetMap();
    private final Map<String, TextureSet> map = new HashMap();

    public static TextureSetMap instance() {
        return INSTANCE;
    }

    public void register(TextureSet set) {
        TextureSet old = this.map.put(set.name, set);
        if (!set.equals(old)) {
            markDirty();
        }
    }

    public TextureSet createAndRegister(ResourceLocation... textures) {
        TextureSet set = new TextureSet(UUID.randomUUID().toString(), textures);
        register(set);
        return set;
    }

    public TextureSet getByName(String name) {
        return this.map.get(name);
    }

    public TextureSet getByNameNonNull(String name) {
        TextureSet set = getByName(name);
        return set == null ? TextureSet.TEST : set;
    }

    public boolean isRegistered(String name) {
        return this.map.containsKey(name);
    }

    public Collection<TextureSet> getAllTextureSets() {
        return this.map.values();
    }

    public Collection<TextureSet> getAllNonStandardTextureSets() {
        List<TextureSet> list = new ArrayList<>(this.map.size());
        for (TextureSet set : this.map.values()) {
            if (!set.isStandard) {
                list.add(set);
            }
        }
        return list;
    }
}
