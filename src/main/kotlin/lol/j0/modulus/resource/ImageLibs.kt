package lol.j0.modulus

import com.mojang.blaze3d.texture.NativeImage
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Item
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.io.IOException
import java.util.stream.Collectors

object ImageLibs {
    /**
     * @author Jamalam360
     */
    fun getNativeImage(item: Item): NativeImage {
        try {
            val id = MinecraftClient.getInstance().itemRenderer.models.getModel(item)!!.particleSprite.id
            val texture = MinecraftClient.getInstance().resourceManager.getAllResources(Identifier(id.namespace, "textures/" + id.path + ".png"))[0]
                    ?: throw NullPointerException("Unexpected null texture")
            return NativeImage.read(texture.open())
        } catch (e: Exception) {
            Modulus.LOGGER.error("Failed to retrieve NativeImage texture of item " + item.name.string + ". This a bug, and should be reported.")
            e.printStackTrace()
        }
        return NativeImage(16, 16, false)
    }

    /**
     * @author LambdAurora
     */
    fun getPaletteFromImage(image: NativeImage): IntList {
        val colors = getColorsFromImage(image)

        // convert the IntStream into a generic stream using `boxed` to be able to supply a custom ordering
        return IntArrayList(colors.intStream().boxed().sorted { color0: Int, color1: Int ->
            val lum0 = luminance(color0)
            val lum1 = luminance(color1)
            java.lang.Float.compare(lum0, lum1)
        }.collect(Collectors.toList()))
    }

    fun getPaletteFromImage(image: NativeImage, expectColors: Int): IntList {
        val palette = getPaletteFromImage(image)
        if (expectColors + 2 < palette.size) {
            val reducedPalette = IntArrayList()
            var lastLuminance = -1f
            for (i in palette.indices) {
                val color = palette.getInt(i)
                val luminance = luminance(color)
                if (MathHelper.abs(luminance - lastLuminance) < 3.1f) continue
                lastLuminance = luminance
                reducedPalette.add(color)
            }
            return reducedPalette
        }
        return palette
    }

    fun getColorsFromImage(image: NativeImage): IntSet {
        val colors = IntOpenHashSet()
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = image.getPixelColor(x, y)
                if (argbUnpackAlpha(color) == 255) {
                    colors.add(color)
                }
            }
        }
        return colors
    }

    fun luminance(color: Int): Float {
        return luminance(argbUnpackRed(color), argbUnpackGreen(color), argbUnpackBlue(color))
    }

    fun luminance(red: Int, green: Int, blue: Int): Float {
        return 0.2126f * red + 0.7152f * green + 0.0722f * blue
    }

    /**
     * Extracts and unpacks the red component of the ARGB color.
     *
     * @param color the ARGB color
     * @return the unpacked red component
     */
    fun argbUnpackRed(color: Int): Int {
        return color shr 16 and 255
    }

    /**
     * Extracts and unpacks the green component of the ARGB color.
     *
     * @param color the ARGB color
     * @return the unpacked green component
     */
    fun argbUnpackGreen(color: Int): Int {
        return color shr 8 and 255
    }

    /**
     * Extracts and unpacks the blue component of the ARGB color.
     *
     * @param color the ARGB color
     * @return the unpacked blue component
     */
    fun argbUnpackBlue(color: Int): Int {
        return color and 255
    }

    /**
     * Extracts and unpacks the alpha component of the ARGB color.
     *
     * @param color the ARGB color
     * @return the unpacked alpha component
     */
    fun argbUnpackAlpha(color: Int): Int {
        return color shr 24 and 255
    }

    /**
     *
     * @author j0lol
     */

    fun itemToImage(identifier: Identifier, resourceManager: ResourceManager): Result4k<NativeImage, TextureNotFoundException> {
        val texturePath = Identifier(
                identifier.namespace,
                "textures/item/" + identifier.path + ".png"
        )
        val resource = resourceManager.getResource(texturePath)
        if (resource.isEmpty) {
            return Failure(TextureNotFoundException())
        }
        try {
            resource.get().open().use { `is` -> return Success(NativeImage.read(`is`)) }
        } catch (e: IOException) {
            return Failure(TextureNotFoundException())
        }
    }
}

class TextureNotFoundException