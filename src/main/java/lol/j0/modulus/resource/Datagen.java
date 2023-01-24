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
import net.minecraft.item.SwordItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import uk.co.samwho.result.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static lol.j0.modulus.Modulus.LOGGER;
import static lol.j0.modulus.resource.DatagenUtils.*;

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
		var stickImage = ModulusUtil.itemToImage(new Identifier("minecraft", "stick"), resourceManager);
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

			// ignore if this tool does not have an equivalent resource.
			if (tool.item.getMaterial().getRepairIngredient().getMatchingStacks().length == 0) {
				continue;
			}

			var image = ModulusUtil.itemToImage(tool.identifier, resourceManager);
			if (image == null) {
				return;
			}

			// find invalid tools: huge textures, no repair ingredients
			if (tool.item.getMaterial().getRepairIngredient().getMatchingStacks().length == 0 || image.getHeight() != 16 || image.getWidth() != 16) {
				continue;
			}

			// i don't want to support swords right now.
			if (tool.item instanceof SwordItem) {
				continue;
			}

			// sanity check: does image have stick palette: if not throw
			var imagePalette = ColorUtil.getPaletteFromImage(image);
			if (!imagePalette.containsAll(stickPalette) ) {
				LOGGER.info("Naive method failed. Giving up.");
				continue;
			}

			// assumption: pickaxe, axe, hoe. shovel is ignored because im lazy. ill do it later
			NativeImage maskedImage = paletteMask(image, stickImage).getOrThrow();

//			NativeImage newImageLeft = new NativeImage(image.getWidth(), image.getHeight(), false);
//			NativeImage newImageRight = new NativeImage(image.getWidth(), image.getHeight(), false);
//
//			for (int x = 0; x < image.getWidth(); x++) {
//				for (int y = 0; y < image.getHeight(); y++) {
//					int pixelColor;
//					if (stickPalette.contains(image.getPixelColor(x,y)) ) {
//						pixelColor = 0x00000000;
//					} else {
//						pixelColor = image.getPixelColor(x,y);
//					}
//
//					if (x <= image.getHeight()-y) {
//						newImageLeft.setPixelColor(x,y,pixelColor);
//					} else {
//						newImageRight.setPixelColor(x,y,pixelColor);
//					}
//				}
//			}
			var splitImage = imageSplitter(maskedImage,  x -> image.getHeight() - x );
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

			// debug
			ModulusClient.RESOURCE_PACK.putImage(new ModelIdentifier(Modulus.id(makeModuleIdString(tool.identifier, "full_dbg")), "inventory"), maskedImage);

			// iterate through each possible item,
			//   sanity check: does image have stick palette: if not throw
			//   palette filter
			//     this could cause issues with Wooden tools, research needed
			//   split image into two at y=x, duplicate or something along mirror line
			//   make model & texture for side A and B
			//   register them to the texture pack
			//   profit!
		}





		// go through stream
        ModuleItem.streamModules().forEach(item -> {
		    // make the item's model

            // make the language entry

            // make the textures
        });
    }

}
