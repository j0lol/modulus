package lol.j0.modulus.resource;

import com.mojang.blaze3d.texture.NativeImage;
import it.unimi.dsi.fastutil.ints.IntList;
import lol.j0.modulus.ColorUtil;
import lol.j0.modulus.Modulus;
import lol.j0.modulus.ModulusUtil;
import lol.j0.modulus.item.ModuleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.registry.api.event.RegistryMonitor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lol.j0.modulus.ImageLibs.getPaletteFromImage;
import static lol.j0.modulus.Modulus.LOGGER;

public final class Datagen {

	private static final List<ToolType> TOOL_TYPES = new ArrayList<>();
	private static final List<WoodType> WOOD_TYPES = new ArrayList<>();
	private static final List<WoodType> SUPPORTED_WOOD_TYPES = new ArrayList<>();
	private static final List<ToolType> SUPPORTED_TOOL_MATERIALS = new ArrayList<>();
	private static final List<String> SUPPORTED_TOOL_TYPES = new ArrayList<>();


	static {
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("oak")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("spruce")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("dark_oak")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("birch")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("acacia")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("jungle")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("mangrove")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("warped")));
		SUPPORTED_WOOD_TYPES.add(new WoodType(new Identifier("crimson")));

		SUPPORTED_TOOL_MATERIALS.add(new ToolType(new Identifier("wooden")));
		SUPPORTED_TOOL_MATERIALS.add(new ToolType(new Identifier("stone")));
		SUPPORTED_TOOL_MATERIALS.add(new ToolType(new Identifier("gold")));
		SUPPORTED_TOOL_MATERIALS.add(new ToolType(new Identifier("iron")));
		SUPPORTED_TOOL_MATERIALS.add(new ToolType(new Identifier("diamond")));
		SUPPORTED_TOOL_MATERIALS.add(new ToolType(new Identifier("netherite")));

		SUPPORTED_TOOL_TYPES.add("pickaxe");
		SUPPORTED_TOOL_TYPES.add("axe");
		SUPPORTED_TOOL_TYPES.add("sword");
		SUPPORTED_TOOL_TYPES.add("hoe");
		SUPPORTED_TOOL_TYPES.add("shovel");

	}

	public static void init() {


		// Tool monitor
		RegistryMonitor.create(Registry.ITEM)
				.filter(context -> context.value() instanceof ToolItem)
				.forAll(context -> {
					var item = (ToolItem) context.value();
					Modulus.LOGGER.info("TOOL MONITOR: " + item.asItem().toString());


					var module_a = new ModuleItem(ModuleItem.MODULE_SIDE.A, item.getMaterial(), new QuiltItemSettings());
					var module_b = new ModuleItem(ModuleItem.MODULE_SIDE.B, item.getMaterial(), new QuiltItemSettings());
					var type = new ToolType(new Identifier(item.asItem().toString()));
					TOOL_TYPES.add(type);

					Modulus.LOGGER.info("TOOL MONITOR: " + type.getId());

					// todo: Turn these tools into parts!
					Registry.register(Registry.ITEM, Modulus.id(type.getName() + "_a"), module_a);
					Registry.register(Registry.ITEM, Modulus.id(type.getName() + "_b"), module_b);

					try {
						toolPaletteStealer(item, module_a);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					try {
						toolPaletteStealer(item, module_b);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});

		// Planks monitor
		RegistryMonitor.create(Registry.BLOCK)
				.filter(context -> context.value().toString().contains("plank"))
				.forAll(context -> {
					var block = context.value();
					var tool_rod = new Item(new QuiltItemSettings());

					Modulus.LOGGER.info("PLANK MONITOR: " + block.asItem().toString());

					var type = new WoodType(new Identifier(block.asItem().toString().split("_planks")[0]));
					WOOD_TYPES.add(type);

					Modulus.LOGGER.info("PLANK MONITOR: " + block.asItem().toString());

					Registry.register(Registry.ITEM, Modulus.id(type.getName() + "_tool_rod"), tool_rod);
					try {
						rodPaletteStealer("oak_stick", block.asItem(), tool_rod);
						rodPaletteStealer("oak_stick_hilt", block.asItem(), tool_rod, "_hilt");
						rodPaletteStealer("oak_stick_small", block.asItem(), tool_rod, "_small");
						model_generator(tool_rod);
						model_generator(tool_rod, "_hilt");
						model_generator(tool_rod, "_small");

					} catch (IOException e) {
						throw new RuntimeException(e);
					}

				});


		//generate_tool_tag(TOOL_TYPES);

		try {
			generate_rod_tag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			generate_tool_tag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Modulus.LOGGER.info(TOOL_TYPES.toString());
		Modulus.LOGGER.info(WOOD_TYPES.toString());



	}

	private static void generate_rod_tag() throws IOException {

		StringBuilder list = new StringBuilder();
		for(int i = 0; i < Datagen.WOOD_TYPES.size(); i++) {
			var woodType = Datagen.WOOD_TYPES.get(i);

			if (i < Datagen.WOOD_TYPES.size() - 1) {
				list.append("    \"modulus:").append(woodType.getName()).append("_tool_rod\",\n");
			} else {
				list.append("    \"modulus:").append(woodType.getName()).append("_tool_rod\"\n");
			}
		}

		var file = new File("../src/main/resources/data/modulus/tags/items/modular_tool_rod.json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var input = "{\n" +
				"  \"replace\": false,\n" +
				"  \"values\": [\n" +
				list +
				"  ]\n" +
				"}";

		Files.writeString(file.toPath(), input);
	}

	private static void generate_tool_tag() throws IOException {

		StringBuilder list = new StringBuilder();
		for (ToolType woodType : Datagen.TOOL_TYPES) {
			list.append("    \"modulus:").append(woodType.getName()).append("_a\",\n");
		}

		for(int i = 0; i < Datagen.TOOL_TYPES.size(); i++) {
			var woodType = Datagen.TOOL_TYPES.get(i);

			if (i < Datagen.TOOL_TYPES.size() - 1) {
				list.append("    \"modulus:").append(woodType.getName()).append("_b\",\n");
			} else {
				list.append("    \"modulus:").append(woodType.getName()).append("_b\"\n");
			}
		}

		var file = new File("../src/main/resources/data/modulus/tags/items/modular_tool_part.json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var input = "{\n" +
				"  \"replace\": false,\n" +
				"  \"values\": [\n" +
				list +
				"  ]\n" +
				"}";

		Files.writeString(file.toPath(), input);
	}

	// For plank -> stick conversion
	public static void rodPaletteStealer(String template, Item victim, Item beneficiary) throws IOException {

		LOGGER.info(Path.of(".").toAbsolutePath().toString());
		FileInputStream is = new FileInputStream("../src/main/resources/assets/modulus/textures/item/templates/" + template + ".png");
		NativeImage baseTexture = NativeImage.read(is);

		IntList basePalette = getPaletteFromImage(baseTexture, 8);

		IntList victimPalette = getPaletteFromImage(NativeImage.read(new FileInputStream("../src/main/resources/assets/modulus/textures/item/palettes/" + victim + ".png")), 8);

		var outputImage = new NativeImage(baseTexture.getWidth(), baseTexture.getHeight(), true);

		for (int y = 0; y < baseTexture.getHeight(); y++) {
			for (int x = 0; x < baseTexture.getWidth(); x++) {
				// Swap the palette of this pixel.
				var paletteIndex = basePalette.indexOf(baseTexture.getPixelColor(x, y));

				if (paletteIndex < 0)
					continue;
				else if (paletteIndex >= victimPalette.size())
					paletteIndex = victimPalette.size() - 1;

				outputImage.setPixelColor(x, y, victimPalette.getInt(paletteIndex));
			}
		}

		outputImage.writeFile(Path.of("../src/main/resources/assets/modulus/textures/item/" + beneficiary + ".png"));

		outputImage.close();
		baseTexture.close();

	}

	public static void rodPaletteStealer(String template, Item victim, Item beneficiary, String suffix) throws IOException {

		LOGGER.info(Path.of(".").toAbsolutePath().toString());
		FileInputStream is = new FileInputStream("../src/main/resources/assets/modulus/textures/item/templates/" + template + ".png");
		NativeImage baseTexture = NativeImage.read(is);

		IntList basePalette = getPaletteFromImage(baseTexture, 8);

		IntList victimPalette = getPaletteFromImage(NativeImage.read(new FileInputStream("../src/main/resources/assets/modulus/textures/item/palettes/" + victim + ".png")), 8);

		var outputImage = new NativeImage(baseTexture.getWidth(), baseTexture.getHeight(), true);

		for (int y = 0; y < baseTexture.getHeight(); y++) {
			for (int x = 0; x < baseTexture.getWidth(); x++) {
				// Swap the palette of this pixel.
				var paletteIndex = basePalette.indexOf(baseTexture.getPixelColor(x, y));

				if (paletteIndex < 0)
					continue;
				else if (paletteIndex >= victimPalette.size())
					paletteIndex = victimPalette.size() - 1;

				outputImage.setPixelColor(x, y, victimPalette.getInt(paletteIndex));
			}
		}

		outputImage.writeFile(Path.of("../src/main/resources/assets/modulus/textures/item/" + beneficiary + suffix + ".png"));

		outputImage.close();
		baseTexture.close();

	}


	public static void toolPaletteStealer(Item victim, Item beneficiary) throws IOException {

		LOGGER.info(Path.of(".").toAbsolutePath().toString());
		FileInputStream is = new FileInputStream("../src/main/resources/assets/modulus/textures/item/templates/diamond_" +
				beneficiary.toString().split("_")[1] + "_" + beneficiary.toString().split("_")[2] + ".png");
		NativeImage baseTexture = NativeImage.read(is);

		IntList basePalette = getPaletteFromImage(baseTexture);

		//IntList victimPalette = getPaletteFromImage(NativeImage.read(new FileInputStream("../src/main/resources/assets/modulus/textures/item/palettes/"
		//		+ victim.toString().split("_")[0] + ".png")));

		var outputImage = new NativeImage(baseTexture.getWidth(), baseTexture.getHeight(), true);
//
//		for (int y = 0; y < baseTexture.getHeight(); y++) {
//			for (int x = 0; x < baseTexture.getWidth(); x++) {
//				// Swap the palette of this pixel.
//				var paletteIndex = basePalette.indexOf(baseTexture.getPixelColor(x, y));
//
//				if (paletteIndex < 0)
//					continue;
//				else if (paletteIndex >= victimPalette.size())
//					paletteIndex = victimPalette.size() - 1;
//
//				outputImage.setPixelColor(x, y, victimPalette.getInt(paletteIndex));
//			}
//		}

	//	outputImage.writeFile(Path.of("../src/main/resources/assets/modulus/textures/item/" + beneficiary + ".png"));
		baseTexture.writeFile(Path.of("../src/main/resources/assets/modulus/textures/item/" + beneficiary + ".png"));

		outputImage.close();
		baseTexture.close();
		model_generator(beneficiary);

	}

	public static void model_generator(Item item) throws IOException {
		var file = new File("../src/main/resources/assets/modulus/models/item/" + item +  ".json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var input = " {\n" +
				"  \"parent\": \"item/handheld\",\n" +
				"  \"textures\": {\n" +
				"    \"layer0\": \"modulus:item/"+ item + "\"\n" +
				"  }\n" +
				"}\n";

		Files.writeString(file.toPath(), input);
	}

	public static void model_generator(Item item, String suffix) throws IOException {
		var file = new File("../src/main/resources/assets/modulus/models/item/" + item +  ".json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var input = " {\n" +
				"  \"parent\": \"item/handheld\",\n" +
				"  \"textures\": {\n" +
				"    \"layer0\": \"modulus:item/"+ item + suffix + "\"\n" +
				"  }\n" +
				"}\n";

		Files.writeString(file.toPath(), input);
	}

	public static void generateClientData(ResourceManager resourceManager) {
	    generateModuleClientData(resourceManager);
	}

    private static void generateModuleClientData(ResourceManager resourceManager) {

		// naive method of texture generation: palette filter the stick texture
		var stickImage = ModulusUtil.itemToImage(new Identifier("minecraft", "stick"), resourceManager);
		if (stickImage == null) {
			return;
		}
		var palette = ColorUtil.getPaletteFromImage(stickImage);

		// loop through all resources: generate each tool.
		// ASSUMPTION CHECK: does each resource have a pick,axe,hoe,sword,shovel?
		//   should this be put somewhere? this should be done in reg-al
		//   idea: ToolType contains each ToolItem relevant, so shenanigans can ensue
		for (ToolType toolType : ToolType.TYPES ) {
			LOGGER.info(toolType.toString());

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
