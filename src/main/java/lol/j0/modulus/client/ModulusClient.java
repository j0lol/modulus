package lol.j0.modulus.client;

import com.mojang.datafixers.util.Pair;
import lol.j0.modulus.Modulus;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.nbt.*;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import static lol.j0.modulus.Modulus.*;


/// All I want to do is layer sprites.... HOWEVER! I need to do this based on the nbt of the item
public class ModulusClient implements ClientModInitializer {

	// Get the vanilla models
	public static final ModelIdentifier MODULAR_TOOL_MODEL = new ModelIdentifier(new Identifier(MOD_ID, "unfinished_modular_tool"), "inventory");
	public static final ModelIdentifier TOOL_ROD = new ModelIdentifier(new Identifier(MOD_ID, "tool_rod"), "inventory");


	private static final MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient(ModContainer mod) {






		Identifier tool = new Identifier(MOD_ID, "tool");

		SpriteIdentifier sprite = new SpriteIdentifier(new Identifier(MOD_ID, "unfinished_modular_tool"), PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

		ArrayList<SpriteIdentifier> SPRITE_REPOSITORY = null;

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODULAR_TOOL_MODEL);
			out.accept(TOOL_ROD);

		});
		LOGGER.info("|initializing|");

		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL, (stack, mode, matrices, vertexConsumers, light, overlay) -> {

			// Stop evil minecraft from transforming twice, destroying our hard work
			matrices.pop();
			matrices.push();

			boolean left = mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;


			// Base model
			BakedModel model = mc.getBakedModelManager().getModel(MODULAR_TOOL_MODEL);


			// todo: replace these checks with ones that dont suck as much
			NbtCompound nbt = stack.getNbt();
			if (nbt != null) {
				NbtList items = (NbtList) nbt.get("Items");
				if (items != null) {

					// If there are items in the tool...

					for (NbtElement i : items) {
						// For each item, get it's nbtCompound, then split into count and id.
						NbtCompound j = (NbtCompound) i;

						int c = (int) j.getByte("Count");


						for (int x = 0; x < c; x++) {

							// Turn the item into it's model, and render it!
							String[] id = j.getString("id").split(":");

							model = mc.getBakedModelManager().getModel(new ModelIdentifier(new Identifier(id[0], id[1]), "inventory"));
							//SPRITE_REPOSITORY.add(new SpriteIdentifier(new Identifier(id[0], id[1]), PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
							LOGGER.info("|modular tool item dbg|" + j.getString("id"));
							mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, model);
						}
					}
				}


				LOGGER.info("|modular tool| " + items);
			} else {
				LOGGER.info("|modular tool| nbt is empty");
			}


			mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, model);
		});

		LOGGER.info("|initialized|");


	}
}

