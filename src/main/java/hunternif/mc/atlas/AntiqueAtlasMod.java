package hunternif.mc.atlas;

import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;

import java.util.logging.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.CreativeTabs;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraftforge.common.Configuration;

public class AntiqueAtlasMod implements ClientModInitializer {
	public static final String ID = "antiqueatlas";
	public static final String NAME = "Antique Atlas";
	public static final String VERSION = "@@MOD_VERSION@@";
	public static final String CHANNEL = ID;
	
	public static AntiqueAtlasMod instance;
	
	public static Logger logger;
	
	public static CommonProxy proxy;
	
	public static ItemAtlas itemAtlas;
	public static ItemEmptyAtlas itemEmptyAtlas;

	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		int atlasItemID = config.getItem("antiqueAtlas", 26949).getInt();
		int emptyAtlasItemID = config.getItem("emptyAntiqueAtlas", 26948).getInt();
		config.save();
		
		itemAtlas = (ItemAtlas) new ItemAtlas(atlasItemID).setUnlocalizedName("antiqueAtlas");
		LanguageRegistry.addName(itemAtlas, "Antique Atlas");
		itemAtlas.setBiomeAnalyzer(ChunkBiomeAnalyzer.instance);
		
		itemEmptyAtlas = (ItemEmptyAtlas) new ItemEmptyAtlas(emptyAtlasItemID)
			.setUnlocalizedName("emptyAntiqueAtlas").setCreativeTab(CreativeTabs.tabTools);
		LanguageRegistry.addName(itemEmptyAtlas, "Empty Antique Atlas");
		
		GameRegistry.addShapelessRecipe(new ItemStack(itemEmptyAtlas), Item.book, Item.compass);
	}
	
	public void init(FMLInitializationEvent event){
		proxy.init(event);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@Override
	public void onInitializeClient() {

	}
}
