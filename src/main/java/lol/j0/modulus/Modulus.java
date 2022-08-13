package lol.j0.modulus;

import lol.j0.modulus.item.ModularToolItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Modulus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Example Mod");
	public static final String MOD_ID = "modulus";


	public static final ModularToolItem MODULAR_TOOL = new ModularToolItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS)); // todo use yttr submodules
	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Hello Quilt world from {}!", mod.metadata().name());
		Registry.register(Registry.ITEM, new Identifier("modulus", "modular_tool"), MODULAR_TOOL);
	}
}
