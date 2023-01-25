package lol.j0.modulus.client;

import lol.j0.modulus.Modulus;
import lol.j0.modulus.item.ModularToolItem;
import lol.j0.modulus.item.ModuleItem;
import lol.j0.modulus.resource.Datagen;
import lol.j0.modulus.resource.ModulusPack;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import net.minecraft.client.MinecraftClient;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

import java.util.ArrayList;
import java.util.Hashtable;


/// All I want to do is layer sprites.... HOWEVER! I need to do this based on the nbt of the item
public class ModulusClient implements ClientModInitializer {

	// Get the vanilla models
	public static final ModelIdentifier MODULAR_TOOL_MODEL = new ModelIdentifier(Modulus.id( "unfinished_modular_tool"), "inventory");
	public static final ModelIdentifier TOOL_ROD = new ModelIdentifier(Modulus.id( "tool_rod"), "inventory");
	public static final ModelIdentifier HOLOGRAM = new ModelIdentifier(Modulus.id( "hologram"), "inventory");
	public static final ModelIdentifier model = new ModelIdentifier(Modulus.id( "birch_tool_rod"), "inventory");

	public Hashtable<String, ModelIdentifier> ModuleModels = new Hashtable<>();
	public Hashtable<String, ModelIdentifier> RodModels = new Hashtable<>();
	private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final ModulusPack RESOURCE_PACK = new ModulusPack(ResourceType.CLIENT_RESOURCES);

	class FunnyRenderingStuff {
		ItemStack stack;
		ModelTransformation.Mode mode;
		MatrixStack matrices;
		VertexConsumerProvider vertexConsumers;
		int light;
		int overlay;
		boolean left;

		FunnyRenderingStuff (ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices,
		                     VertexConsumerProvider vertexConsumers, int light, int overlay) {
			this.stack = stack;
			this.mode = mode;
			this.matrices = matrices;
			this.vertexConsumers = vertexConsumers;
			this.light = light;
			this.overlay = overlay;
			this.left = mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;

			matrices.pop();
			matrices.push();
		}

		public void render(BakedModel bakedModel) {
			ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(model));
			mc.getItemRenderer().renderItem(stack, mode, left, matrices, vertexConsumers, light, overlay, bakedModel);
		}
	}
    @Override
	public void onInitializeClient(ModContainer mod) {
	    ResourceLoader.get(ResourceType.CLIENT_RESOURCES).getRegisterDefaultResourcePackEvent().register(context -> {
		  context.addResourcePack(RESOURCE_PACK.rebuild(ResourceType.CLIENT_RESOURCES, context.resourceManager()));
		});

		/* this needs to be simplified */

		var list = guessModelNames();
		var tool_rod_list = guessToolRodModelNames();
		for (String i: list) {
			ModuleModels.put(i, new ModelIdentifier(Modulus.id(i), "inventory"));
		}
		for (String i: tool_rod_list) {
			RodModels.put(i, new ModelIdentifier(Modulus.id(i), "inventory"));
		}

		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODULAR_TOOL_MODEL);
			out.accept(TOOL_ROD);
			out.accept(HOLOGRAM);

			for (ModelIdentifier modelIdentifier : Datagen.CREATED_MODELS ) {
				out.accept(modelIdentifier);
			}

//			out.accept(ModuleModels.get("module_diamond_axe_a"));
//			out.accept(ModuleModels.get("module_diamond_axe_b"));
//			out.accept(ModuleModels.get("flipped_module_diamond_axe_a"));
//			out.accept(ModuleModels.get("flipped_module_diamond_axe_b"));
//			//out.accept(MODULE);
		});

		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULE, ((stack, mode, matrices, vertexConsumers, light, overlay) -> {
			FunnyRenderingStuff renderer = new FunnyRenderingStuff(stack, mode, matrices, vertexConsumers, light, overlay);

			renderer.render(mc.getBakedModelManager().getModel(ModuleItem.getModelID(stack)));
		}));
		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.TOOL_ROD, ((stack, mode, matrices, vertexConsumers, light, overlay) -> {
			FunnyRenderingStuff renderer = new FunnyRenderingStuff(stack, mode, matrices, vertexConsumers, light, overlay);

//			var model_name = stack.getOrCreateNbt().getString("material") + "_tool_rod";
//			if (RodModels.containsKey(model_name)) {
//				renderer.render(mc.getBakedModelManager().getModel(RodModels.get(model_name)));
//			} else {
			renderer.render(mc.getBakedModelManager().getModel(TOOL_ROD));
//			}

		}));

		BuiltinItemRendererRegistry.INSTANCE.register(Modulus.MODULAR_TOOL, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			FunnyRenderingStuff renderer = new FunnyRenderingStuff(stack, mode, matrices, vertexConsumers, light, overlay);


			if (mode == ModelTransformation.Mode.GUI || !ModularToolItem.getIfEditable(stack) ) {

				if (ModularToolItem.getIfEditable(stack)) {
					renderer.render(mc.getBakedModelManager().getModel(MODULAR_TOOL_MODEL));
				}


				NbtList items = ModularToolItem.getModuleList(stack);
				if (!items.isEmpty()) {
					// If there are items in the tool...
					// For each item, get it's nbtCompound, then split into count and id.
					for (NbtElement item : items) {
						// Turn the item into it's model, and render it!
						Item module = ItemStack.fromNbt((NbtCompound) item).getItem();
						ItemStack moduleStack = ItemStack.fromNbt((NbtCompound) item);

						if( moduleStack.isOf(Modulus.MODULE) ){
							renderer.render(mc.getBakedModelManager().getModel(ModuleItem.getModelID(moduleStack)));

						} else if (moduleStack.isOf(Modulus.TOOL_ROD)) {
							renderer.render(mc.getBakedModelManager().getModel(TOOL_ROD));
						} else {
							renderer.render(mc.getBakedModelManager().getModel(new ModelIdentifier(Registry.ITEM.getId(module), "inventory")));
						}
					}
				} else if (!ModularToolItem.getIfEditable(stack)) {
					renderer.render(mc.getBakedModelManager().getModel(HOLOGRAM));

				}
			}
			else {
				renderer.render(mc.getBakedModelManager().getModel(HOLOGRAM));
			}
		});
	}

	public ArrayList<String> guessModelNames() {
		String[] resources = new String[]{"wooden", "stone", "iron", "golden", "diamond", "netherite"};
		String[] types = new String[]{"axe", "hoe", "pickaxe", "sword", "shovel"};
		var out = new ArrayList<String>();

		for (String r: resources) {
			for (String t: types) {
				out.add("module_" + r + "_" + t + "_a");
				out.add("module_" + r + "_" + t + "_b");
				if (t.equals("axe") || t.equals("hoe")) {
					out.add("flipped_module_" + r + "_" + t + "_a");
					out.add("flipped_module_" + r + "_" + t + "_b");
				}
			}
		}
		return out;
	}

	public ArrayList<String> guessToolRodModelNames() {
		String[] resources = new String[]{"vanilla", "acacia", "birch", "crimson", "dark_oak", "jungle", "mangrove", "oak", "spruce", "warped"};
		var out = new ArrayList<String>();

		for (String r: resources) {
				out.add(r + "_tool_rod");
		}
		return out;
	}
}

