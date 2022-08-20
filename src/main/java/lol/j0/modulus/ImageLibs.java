package lol.j0.modulus;


import com.mojang.blaze3d.texture.NativeImage;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.stream.Collectors;

public class ImageLibs {


	/**
	 * @author Jamalam360
	 */
	public static NativeImage getNativeImage(Item item) {
		try {
			Identifier id = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(item).getParticleSprite().getId();

			Resource texture = MinecraftClient.getInstance().getResourceManager().getAllResources(new Identifier(id.getNamespace(), "textures/" + id.getPath() + ".png")).get(0);

			if (texture == null) {
				throw new NullPointerException("Unexpected null texture");
			}

			return NativeImage.read(texture.open());
		} catch (Exception e) {
			Modulus.LOGGER.error("Failed to retrieve NativeImage texture of item " + item.getName().getString() + ". This a bug, and should be reported.");
			e.printStackTrace();
		}

		return new NativeImage(16, 16, false);
	}


	/**
	 * @author LambdAurora
	 */
	public static IntList getPaletteFromImage(NativeImage image) {
		var colors = getColorsFromImage(image);

		// convert the IntStream into a generic stream using `boxed` to be able to supply a custom ordering
		return new IntArrayList(colors.intStream().boxed().sorted((color0, color1) -> {
			var lum0 = luminance(color0);
			var lum1 = luminance(color1);

			return Float.compare(lum0, lum1);
		}).collect(Collectors.toList()));
	}

	public static IntList getPaletteFromImage(NativeImage image, int expectColors) {
		var palette = getPaletteFromImage(image);

		if (expectColors + 2 < palette.size()) {
			var reducedPalette = new IntArrayList();

			float lastLuminance = -1.f;

			for (int i = 0; i < palette.size(); i++) {
				int color = palette.getInt(i);
				float luminance = luminance(color);

				if (MathHelper.abs(luminance - lastLuminance) < 3.1f) continue;

				lastLuminance = luminance;

				reducedPalette.add(color);
			}

			return reducedPalette;
		}

		return palette;
	}

	public static IntSet getColorsFromImage(NativeImage image) {
		var colors = new IntOpenHashSet();

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int color = image.getPixelColor(x, y);

				if (argbUnpackAlpha(color) == 255) {
					colors.add(color);
				}
			}
		}

		return colors;
	}



	public static float luminance(int color) {
		return luminance(argbUnpackRed(color), argbUnpackGreen(color), argbUnpackBlue(color));
	}

	public static float luminance(int red, int green, int blue) {
		return (0.2126f * red + 0.7152f * green + 0.0722f * blue);
	}


	/**
	 * Extracts and unpacks the red component of the ARGB color.
	 *
	 * @param color the ARGB color
	 * @return the unpacked red component
	 */
	public static int argbUnpackRed(int color) {
		return (color >> 16) & 255;
	}

	/**
	 * Extracts and unpacks the green component of the ARGB color.
	 *
	 * @param color the ARGB color
	 * @return the unpacked green component
	 */
	public static int argbUnpackGreen(int color) {
		return (color >> 8) & 255;
	}

	/**
	 * Extracts and unpacks the blue component of the ARGB color.
	 *
	 * @param color the ARGB color
	 * @return the unpacked blue component
	 */
	public static int argbUnpackBlue(int color) {
		return color & 255;
	}

	/**
	 * Extracts and unpacks the alpha component of the ARGB color.
	 *
	 * @param color the ARGB color
	 * @return the unpacked alpha component
	 */
	public static int argbUnpackAlpha(int color) {
		return (color >> 24) & 255;
	}

}
