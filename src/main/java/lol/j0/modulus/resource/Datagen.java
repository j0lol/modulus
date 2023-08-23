package lol.j0.modulus.resource;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.texture.NativeImage;
import lol.j0.modulus.ColorUtil;
import lol.j0.modulus.Modulus;
import lol.j0.modulus.ModulusUtil;
import lol.j0.modulus.client.ModulusClient;
import lol.j0.modulus.item.ModuleItem;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import uk.co.samwho.result.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import static lol.j0.modulus.Modulus.LOGGER;
import static lol.j0.modulus.ModulusUtil.itemToImage;
import static lol.j0.modulus.resource.DatagenUtils.*;
import static lol.j0.modulus.resource.DatagenUtils.imageInvertedMask;

public final class Datagen {
	private final JsonObject json = new JsonObject();

	public static ArrayList<ModelIdentifier> CREATED_MODELS;


	static {
		CREATED_MODELS = new ArrayList<>();
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
		for (Tool tool : ToolType.DISCOVERED_TOOLS ) {

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
		}
    }

	public static Result<NativeImage> maskImage(NativeImage target, Tool tool, ResourceManager resourceManager) {

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
	private static Result<NativeImage> methodB(NativeImage target, Tool tool, ResourceManager resourceManager) {
		var lastIndex = tool.identifier.getPath().lastIndexOf("_");
		var toolType = tool.identifier.getPath().substring(lastIndex + 1);

		var diamondTool = ModulusUtil.itemToImage(new Identifier("minecraft", "diamond_" + toolType), resourceManager).get();
		var diamondHead = methodA(diamondTool, resourceManager).get();

		return Result.success(imageInvertedMask(target, diamondHead));
	}

}
