package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasItem;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.*;

public class ItemEmptyAtlas extends ItemMapBase {
    public ItemEmptyAtlas(int id) {
        super(id, "emptyAntiqueAtlas");
        setUnlocalizedName("emptyAntiqueAtlas");
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
        if (player.onClient()) {
            return true;
        }
        WorldServer world = player.getWorldServer();
        if (world.peekUniqueDataId(ItemAtlas.WORLD_ATLAS_DATA_ID) >= 32000) {
            return false;
        }
        int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_ATLAS_DATA_ID);
        ItemStack atlasStack = new ItemStack(AntiqueAtlasItem.itemAtlas, 1, atlasID);
        String atlasKey = AntiqueAtlasItem.itemAtlas.getAtlasDataKey(atlasID);
        AtlasData atlasData = new AtlasData(atlasKey);
        world.setItemData(atlasKey, atlasData);
        String markersKey = AntiqueAtlasItem.itemAtlas.getMarkersDataKey(atlasID);
        MarkersData markersData = new MarkersData(markersKey);
        world.setItemData(markersKey, markersData);
        player.inventory.convertOneOfCurrentItem(atlasStack);
        return true;
    }
}
