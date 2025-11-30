package hunternif.mc.atlas;

import huix.glacier.api.entrypoint.IGameRegistry;
import huix.glacier.api.registry.MinecraftRegistry;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import net.xiaoyu233.fml.reload.utils.IdUtil;

public class AntiqueAtlasItem implements IGameRegistry {
    public static ItemAtlas itemAtlas = new ItemAtlas(IdUtil.getNextItemID());
    public static ItemEmptyAtlas emptyAtlas = new ItemEmptyAtlas(IdUtil.getNextItemID());
    public static final MinecraftRegistry registry = new MinecraftRegistry(AntiqueAtlasMod.NAME).initAutoItemRegister();

    @Override
    public void onGameRegistry() {
        registry.registerItem("antiqueatlas:antiqueAtlas", "antiqueAtlas", itemAtlas);
        registry.registerItem("antiqueatlas:emptyAntiqueAtlas", "emptyAntiqueAtlas", emptyAtlas);
    }
}
