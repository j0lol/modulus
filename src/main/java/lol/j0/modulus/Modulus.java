package lol.j0.modulus;

import lol.j0.modulus.item.ModularToolItem;
import lol.j0.modulus.item.ToolHammerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

public class Modulus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("|MODULUS|");
	public static final String MOD_ID = "modulus";

	public static final ModularToolItem MODULAR_TOOL = new ModularToolItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS)); // todo use yttr submodules
	public static final Item TOOL_ROD = new Item(new QuiltItemSettings().maxCount(64).group(ItemGroup.TOOLS));
	public static final ToolHammerItem TOOL_HAMMER = new ToolHammerItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS));


	public static final Item DIAMOND_PICKAXE_L = new Item(new QuiltItemSettings().maxCount(64).group(ItemGroup.TOOLS));
	public static final Item DIAMOND_PICKAXE_R = new Item(new QuiltItemSettings().maxCount(64).group(ItemGroup.TOOLS));

	public Dictionary ITEM_TIERS = new Hashtable();

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.ITEM, Modulus.id("modular_tool"), MODULAR_TOOL);
		Registry.register(Registry.ITEM, Modulus.id("tool_rod"), TOOL_ROD);
		Registry.register(Registry.ITEM, Modulus.id( "tool_hammer"), TOOL_HAMMER);
		Registry.register(Registry.ITEM, Modulus.id( "diamond_pickaxe_l"), DIAMOND_PICKAXE_L);
		Registry.register(Registry.ITEM, Modulus.id( "diamond_pickaxe_r"), DIAMOND_PICKAXE_R);


		// 				tier lookup,	 	durability, miningspeedmultiplier, attack multiplier, mininglevel, enchantability, repair item
		//									 L,D,s,d,E,ingred
//		ITEM_TIERS.put("DIAMOND", new Item[]{3,,,3.0F,10,Items.DIAMOND});
//		ITEM_TIERS.put("WOOD", new Item[]{0,59,2.0F,0.0F,15,Items.OAK_PLANKS});


	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}


}
