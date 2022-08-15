package lol.j0.modulus;

import lol.j0.modulus.item.ModularToolItem;
import lol.j0.modulus.item.ToolHammerItem;
import lol.j0.modulus.item.UnfinishedModularToolItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Modulus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("|MODULUS|");
	public static final String MOD_ID = "modulus";

	public static final ModularToolItem MODULAR_TOOL = new ModularToolItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS)); // todo use yttr submodules
	public static final UnfinishedModularToolItem UNFINISHED_MODULAR_TOOL = new UnfinishedModularToolItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS)); // todo use yttr submodules
	public static final Item TOOL_ROD = new Item(new QuiltItemSettings().maxCount(64).group(ItemGroup.TOOLS));
	public static final ToolHammerItem TOOL_HAMMER = new ToolHammerItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS));


	public static final Item DIAMOND_PICKAXE_L = new Item(new QuiltItemSettings().maxCount(64).group(ItemGroup.TOOLS));
	public static final Item DIAMOND_PICKAXE_R = new Item(new QuiltItemSettings().maxCount(64).group(ItemGroup.TOOLS));


	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "modular_tool"), MODULAR_TOOL);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "unfinished_modular_tool"), UNFINISHED_MODULAR_TOOL);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tool_rod"), TOOL_ROD);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "tool_hammer"), TOOL_HAMMER);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "diamond_pickaxe_l"), DIAMOND_PICKAXE_L);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "diamond_pickaxe_r"), DIAMOND_PICKAXE_R);
	}
}
