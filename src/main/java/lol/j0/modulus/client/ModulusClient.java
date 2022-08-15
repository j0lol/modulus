package lol.j0.modulus.client;

import lol.j0.modulus.Modulus;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import net.minecraft.client.MinecraftClient;

import static lol.j0.modulus.Modulus.*;


/// All I want to do is layer sprites.... HOWEVER! I need to do this based on the nbt of the item
public class ModulusClient implements ClientModInitializer {

	// Get the vanilla models
	public static final ModelIdentifier MODULAR_TOOL_MODEL = new ModelIdentifier(new Identifier(MOD_ID, "unfinished_modular_tool"), "inventory");
	public static final ModelIdentifier TOOL_ROD = new ModelIdentifier(new Identifier(MOD_ID, "tool_rod"), "inventory");


	private static final MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient(ModContainer mod) {

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODULAR_TOOL_MODEL);
			out.accept(TOOL_ROD);

		});

		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL, (stack, mode, matrices, vertexConsumers, light, overlay) -> {

			// Stop evil minecraft from transforming twice, destroying our hard work
			matrices.pop();
			matrices.push();

			boolean left = mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;

			BakedModel model;

			// todo: replace these checks with ones that dont suck as much
			NbtCompound nbt = stack.getNbt();
			if (nbt != null) {

				if (!nbt.getBoolean("Finished")) {
					model = mc.getBakedModelManager().getModel(MODULAR_TOOL_MODEL);
					mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, model);
				}

				NbtList items = (NbtList) nbt.get("Items");
				if (items != null) {

					// If there are items in the tool...

					for (NbtElement item : items) {
						// For each item, get it's nbtCompound, then split into count and id.
						NbtCompound itemAsCompound = (NbtCompound) item;
						int count = itemAsCompound.getByte("Count");
						String ID = itemAsCompound.getString("id");


						for (int x = 0; x < count; x++) {

							// Turn the item into it's model, and render it!

							String[] SplitID = ID.split(":");
							model = mc.getBakedModelManager().getModel(new ModelIdentifier(new Identifier(SplitID[0], SplitID[1]), "inventory"));
							mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, model);
						}
					}
				}
			}
		});


	}
}

