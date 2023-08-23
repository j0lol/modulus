package lol.j0.modulus.resource;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.texture.NativeImage;
import lol.j0.modulus.ColorUtil;
import lol.j0.modulus.Modulus;
import lol.j0.modulus.ModulusUtil;
import lol.j0.modulus.api.RegisteredTool;
import lol.j0.modulus.client.ModulusClient;
import lol.j0.modulus.registry.ToolRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.quiltmc.qsl.registry.api.event.RegistryEntryContext;
import uk.co.samwho.result.Result;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.Objects;

import static lol.j0.modulus.Modulus.LOGGER;
import static lol.j0.modulus.Modulus.id;
import static lol.j0.modulus.ModulusUtil.itemToImage;
import static lol.j0.modulus.resource.DatagenUtils.*;
import static lol.j0.modulus.resource.DatagenUtils.imageInvertedMask;

public final class Datagen {

	public static class DiscoveredTool {
		Identifier identifier;
		ToolItem item;

		DiscoveredTool(Identifier identifier, ToolItem item) {
			this.identifier = identifier;
			this.item = item;
		}
	}
	private final JsonObject json = new JsonObject();

	public static ArrayList<ModelIdentifier> CREATED_MODELS;
	public static ArrayList<DiscoveredTool> DISCOVERED_TOOLS;


	static {
		CREATED_MODELS = new ArrayList<>();
		DISCOVERED_TOOLS = new ArrayList<>();
	}

	public static boolean filterItems(RegistryEntryContext<Item> context) {
		if (!(context.value() instanceof ToolItem)) return false;
		return true;
	}

	public static void discoverItems(RegistryEntryContext<Item> context) {
		Datagen.DISCOVERED_TOOLS.add(new DiscoveredTool(context.id(), (ToolItem) context.value()));
	}


	public static void generateClientData(ResourceManager resourceManager) {
	    try {
			generateModuleClientData(resourceManager);
		} catch (Exception e) {
			LOGGER.info("Generating module client data failed.");
		}
	}

    private static void generateModuleClientData(ResourceManager resourceManager) throws Exception {

		// naive method of texture generation: palette filter the stick texture
		var stickImage = itemToImage(new Identifier("minecraft", "stick"), resourceManager).getOrThrow();
		if (stickImage == null) {
			return;
		}
		var stickPalette = ColorUtil.getPaletteFromImage(stickImage);

		// loop through all resources: generate each tool.
		// ASSUMPTION CHECK: does each resource have a pick,axe,hoe,sword,shovel?
		//   should this be put somewhere? this should be done in reg-al
		//   idea: ToolType contains each ToolItem relevant, so shenanigans can ensue
		LOGGER.info("Starting resource generation...");
		for (DiscoveredTool tool : DISCOVERED_TOOLS ) {

			var image = itemToImage(tool.identifier, resourceManager).getOrThrow();
			if (image == null) {
				return;
			}

			// find invalid tools: huge textures, no repair ingredients
			if (tool.item.getMaterial().getRepairIngredient().getMatchingStacks().length == 0 || image.getHeight() != 16 || image.getWidth() != 16) {
				LOGGER.info("Nonstandard tool. Giving up on " + tool.identifier);
				//continue;
			}

			// i don't want to support swords right now.
			if (tool.item instanceof SwordItem || tool.item instanceof ShovelItem) {
				LOGGER.info("Unsupported tool type. Giving up on " + tool.identifier);
				continue;
			}

			Result<Pair<NativeImage,NativeImage>> splitImage = imageSplitter(maskImage(image, tool, resourceManager)
					.get(), x -> image.getHeight() - x);


			NativeImage newImageLeft = splitImage.getOrThrow().getLeft();
			NativeImage newImageRight = splitImage.getOrThrow().getRight();

			var textureIdLeft = Modulus.id("item/" + makeModuleIdString(tool.identifier, "a"));
			var textureIdRight = Modulus.id("item/" + makeModuleIdString(tool.identifier, "b"));

			// build & put models
			modelBuilder(new Identifier("item/handheld"))
				.texture("layer0", textureIdLeft)
				.register(textureIdLeft);
			modelBuilder(new Identifier("item/handheld"))
					.texture("layer0", textureIdRight)
					.register(textureIdRight);

			CREATED_MODELS.add(new ModelIdentifier(Modulus.id(makeModuleIdString(tool.identifier, "a")),"inventory"));
			CREATED_MODELS.add(new ModelIdentifier(Modulus.id(makeModuleIdString(tool.identifier, "b")),"inventory"));

			// add item to valid_tools tag
			// ModulusClient.RESOURCE_PACK.open(ResourceType.CLIENT_RESOURCES, ) ??

			// put image
			ModulusClient.RESOURCE_PACK.putImage(textureIdLeft, newImageLeft);
			ModulusClient.RESOURCE_PACK.putImage(textureIdRight, newImageRight);

			// add tool to "registry"
			ToolRegistry.register(tool.identifier, tool.item);
		}
    }

	public static Result<NativeImage> maskImage(NativeImage target, DiscoveredTool tool, ResourceManager resourceManager) {

		var stickImage = itemToImage(new Identifier("minecraft", "stick"), resourceManager).get();
		var stickPalette = ColorUtil.getPaletteFromImage(stickImage);

		if (Objects.equals(tool.identifier.getNamespace(), "minecraft")) {
			return methodB(target, tool, resourceManager);
		}
		LOGGER.info(stickPalette.toString());

		if (ColorUtil.getPaletteFromImage(target).intStream().anyMatch(stickPalette::contains)) {
			LOGGER.info("Method A on " + tool.identifier);
			return methodA(target, resourceManager);
		} else {
			LOGGER.info("Method B on " + tool.identifier);
			return methodB(target, tool, resourceManager);
		}
	}
	private static Result<NativeImage> methodA(NativeImage target, ResourceManager resourceManager) {
		var stickImage = itemToImage(new Identifier("minecraft", "stick"), resourceManager).get();
		return paletteMask(target, stickImage);
	}
	private static Result<NativeImage> methodB(NativeImage target, DiscoveredTool tool, ResourceManager resourceManager) {
		var lastIndex = tool.identifier.getPath().lastIndexOf("_");
		var toolType = tool.identifier.getPath().substring(lastIndex + 1);

		var diamondTool = ModulusUtil.itemToImage(new Identifier("minecraft", "diamond_" + toolType), resourceManager).get();
		var diamondHead = methodA(diamondTool, resourceManager).get();

		return Result.success(imageInvertedMask(target, diamondHead));
	}

}
