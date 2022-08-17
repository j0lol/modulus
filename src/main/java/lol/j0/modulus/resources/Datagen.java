package lol.j0.modulus.resources;

import lol.j0.modulus.Modulus;
import net.minecraft.item.ToolItem;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.registry.api.event.RegistryMonitor;

public class Datagen {
	public static void init() {

		RegistryMonitor.create(Registry.ITEM)
				.filter(context -> context.value() instanceof ToolItem)
				.forAll(context -> {
					var item = (ToolItem) context.value();

					// You have every tool. Now what? What to do with this newfound power?
					Modulus.LOGGER.info(item.asItem().toString());

				});
	}
	// OH god

	// uhh

	// - Do some registry analysis
	// - Find all tool materials:
		// 	private static final List<SignPostItem> SIGN_POSTS = new ArrayList<>();
	// - Get base pick/axe/shovel/etc
	// -
}
