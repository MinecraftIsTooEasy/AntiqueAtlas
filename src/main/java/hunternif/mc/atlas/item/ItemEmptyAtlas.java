package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.IconRegister;
import net.minecraft.EntityPlayer;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.World;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas(int id) {
		super();
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(AntiqueAtlasMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) return stack;
		
		int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_DATA_ID);
		ItemStack atlasStack = new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID);
		
		String key = AntiqueAtlasMod.itemAtlas.getDataKey(atlasID);
		AtlasData data = new AtlasData(key);
		world.setItemData(key, data);
		
		stack.stackSize--;
		if (stack.stackSize <= 0) {
			return atlasStack;
		} else {
			if (!player.inventory.addItemStackToInventory(atlasStack.copy())) {
				player.dropPlayerItem(atlasStack);
			}
			return stack;
		}
	}
}
