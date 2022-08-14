package lol.j0.modulus.client;

import lol.j0.modulus.Modulus;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import net.minecraft.client.MinecraftClient;

import static lol.j0.modulus.Modulus.*;


public class ModulusClient implements ClientModInitializer {

	public static final ModelIdentifier MODULAR_TOOL_MODEL = new ModelIdentifier(new Identifier(MOD_ID, "unfinished_modular_tool"), "inventory");
	public static final ModelIdentifier TOOL_ROD = new ModelIdentifier(new Identifier(MOD_ID, "tool_rod"), "inventory");
	private static final MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitializeClient(ModContainer mod) {

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODULAR_TOOL_MODEL);
			out.accept(TOOL_ROD);
		});
		LOGGER.info("|initializing|");

		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			boolean left = mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;


			BakedModel model = mc.getBakedModelManager().getModel(MODULAR_TOOL_MODEL);
			if (stack.getNbt() != null) {
				NbtList items = (NbtList) stack.getNbt().get("Items");

				for (NbtElement i : items) {
					NbtCompound j = (NbtCompound) i;

					int c = (int) j.getByte("Count");

					for (int x = 0; x < c; x++) {
						String[] id = j.getString("id").split(":");
						model = mc.getBakedModelManager().getModel(new ModelIdentifier(new Identifier(id[0], id[1]), "inventory"));
						LOGGER.info("|modular tool item dbg|" + j.getString("id"));
					}
				}

				//LOGGER.info("|modular tool| " + items);
				//model = mc.getBakedModelManager().getModel(TOOL_ROD);
			} else {
				//LOGGER.info("|modular tool| modulus can't read stack nbt");
			}

			mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, model);
		});

		LOGGER.info("|initialized|");


	}
}

//	object RetroComputersClient : ClientModInitializer {
//		override fun onInitializeClient() {
//			Shaders.init()
//
//			ModelLoadingRegistry.INSTANCE.registerVariantProvider {
//				val r = RendererAccess.INSTANCE.renderer
//				if (r != null) {
//					val model = UnbakedWireModel(r, Identifier(MOD_ID, "block/ribbon_cable"), 0.5f, 0.0625f, 32.0f, ConcurrentHashMap())
//					ModelVariantProvider { modelId, _ -> model.takeIf { Identifier(modelId.namespace, modelId.path) == Registry.BLOCK.getId(RetroComputers.blocks.ribbonCable) } }
//				} else {
//					RetroComputers.logger.error("Could not find Renderer API implementation. Rendering for Ribbon Cables will not be available.")
//					ModelVariantProvider { _, _ -> null }
//				}
//			}
//		}
//	}

//	public class RetroComputersClient implements ClientModInitializer {
//		@Override
//		public void onInitializeClient() {
//			Shaders.init();
//
//			ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> {
//				var r = RendererAccess.INSTANCE.getRenderer();
//				if (r != null) {
//					var model = new UnbakedWireModel(r, new Identifier(MOD_ID, "block/ribbon_cable"), 0.5f, 0.0625f, 32.0f, new ConcurrentHashMap());
//					return (modelId, context) -> {
//						Identifier id = new Identifier(modelId.getNamespace(), modelId.getPath());
//						return id.equals(Registry.BLOCK.getId(RetroComputers.blocks.ribbonCable))?  model : null;
//					}
//				} else {
//					RetroComputers.logger.error("Could not find Renderer API implementation. Rendering for Ribbon Cables will not be available.");
//					return (modelId, context) -> null;
//				}
//			});
//		}
//	}
