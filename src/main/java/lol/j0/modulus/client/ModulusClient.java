package lol.j0.modulus.client;

import lol.j0.modulus.Modulus;
import lol.j0.modulus.item.ModularToolItem;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import net.minecraft.client.MinecraftClient;


/// All I want to do is layer sprites.... HOWEVER! I need to do this based on the nbt of the item
public class ModulusClient implements ClientModInitializer {

	// Get the vanilla models
	public static final ModelIdentifier MODULAR_TOOL_MODEL = new ModelIdentifier(Modulus.id( "unfinished_modular_tool"), "inventory");
	public static final ModelIdentifier TOOL_ROD = new ModelIdentifier(Modulus.id( "tool_rod"), "inventory");
	public static final ModelIdentifier HOLOGRAM = new ModelIdentifier(Modulus.id( "hologram"), "inventory");

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient(ModContainer mod) {

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODULAR_TOOL_MODEL);
			out.accept(TOOL_ROD);
			out.accept(HOLOGRAM);

		});

		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL, (stack, mode, matrices, vertexConsumers, light, overlay) -> {

			// Stop evil minecraft from transforming twice, destroying our hard work
			matrices.pop();
			matrices.push();

			boolean left = mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;


			if (mode == ModelTransformation.Mode.GUI || !ModularToolItem.getIfEditable(stack) ) {

				if (ModularToolItem.getIfEditable(stack)) {
					mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay,
							mc.getBakedModelManager().getModel(MODULAR_TOOL_MODEL)
					);
				}


				NbtList items = ModularToolItem.getModuleList(stack);
				if (!items.isEmpty()) {
					// If there are items in the tool...
					// For each item, get it's nbtCompound, then split into count and id.
					for (NbtElement item : items) {
						// Turn the item into it's model, and render it!
						Item module = ItemStack.fromNbt((NbtCompound) item).getItem();

						mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay,
								mc.getBakedModelManager().getModel(new ModelIdentifier(Registry.ITEM.getId(module), "inventory"))
						);
					}
				} else if (!ModularToolItem.getIfEditable(stack)) {
					mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay,
							mc.getBakedModelManager().getModel(HOLOGRAM)
					);
				}
			}
			else {
				mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay,
						mc.getBakedModelManager().getModel(HOLOGRAM)
				);
			}

		});


	}
}

