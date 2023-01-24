package lol.j0.modulus.resource;

import com.mojang.blaze3d.texture.NativeImage;
import lol.j0.modulus.ColorUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import uk.co.samwho.result.Result;
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


public class DatagenUtils {


	public static ModelBuilder modelBuilder(Identifier parent) {
		return new ModelBuilder(parent);
	}

	public static String makeModuleIdString(Identifier identifier, String side) {
		return identifier.getNamespace() + "/" + identifier.getPath() + "/" + side;
	}

	/**
	 * Takes an image, and returns a new image, with any colors removed that are contained in the mask.
	 *
	 * @param target Image to be mask
	 * @param mask Masker
	 * @return Returns resultant image wrapped in a Result. Can also return an error.
	 */
	public static Result<NativeImage> paletteMask(NativeImage target, NativeImage mask) {
		var maskPalette = ColorUtil.getPaletteFromImage(mask);

		if (ColorUtil.getPaletteFromImage(target).intStream().noneMatch(maskPalette::contains)) {
			return Result.fail(new Exception("Cannot mask, image does not contain any colors from mask."));
		}

		var outImage = new NativeImage(target.getWidth(), target.getHeight(), false);
		for (int x = 0; x < target.getWidth(); x++) {
			for (int y = 0; y < target.getHeight(); y++) {
				int pixelColor;
				if (maskPalette.contains(target.getPixelColor(x,y)) ) {
					pixelColor = 0x00000000;
				} else {
					pixelColor = target.getPixelColor(x,y);
				}
				outImage.setPixelColor(x,y,pixelColor);
			}
		}
		return Result.success(outImage);
	}

	// fixme: this can cause "debris" pixels?
	public static Result<Pair<NativeImage, NativeImage>> imageSplitter(NativeImage image, LinearFunction func) {
		NativeImage newImageLeft = new NativeImage(image.getWidth(), image.getHeight(), false);
		NativeImage newImageRight = new NativeImage(image.getWidth(), image.getHeight(), false);

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {

				var pixel = image.getPixelColor(x, y);

				if (y < func.apply(x)) {
					newImageLeft.setPixelColor(x, y, pixel);
				} else if (y == func.apply(x)) {
					newImageLeft.setPixelColor(x, y, pixel);
					newImageRight.setPixelColor(x, y, pixel);
				} else {
					newImageRight.setPixelColor(x, y, pixel);
				}
			}
		}
		return Result.success(new Pair<>(newImageLeft, newImageRight));
	}

	@FunctionalInterface
	interface LinearFunction {
		double apply(double x);
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
}
