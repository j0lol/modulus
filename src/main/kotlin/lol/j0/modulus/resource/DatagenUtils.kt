package lol.j0.modulus.resource

import com.mojang.blaze3d.texture.NativeImage
import it.unimi.dsi.fastutil.ints.IntList
import lol.j0.modulus.ColorUtil
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.Pair
import uk.co.samwho.result.Result
import java.io.File
import java.io.IOException
import java.nio.file.Files

object DatagenUtils {
    fun modelBuilder(parent: Identifier?): ModelBuilder {
        return ModelBuilder(parent!!)
    }

    fun makeModuleIdString(identifier: Identifier, side: String): String {
        return identifier.namespace + "/" + identifier.path + "/" + side
    }

    /**
     * Takes an image, and returns a new image, with any colors removed that are contained in the mask.
     *
     * @param target Image to be mask
     * @param mask Masker
     * @return Returns resultant image wrapped in a Result. Can also return an error.
     */
    fun paletteMask(target: NativeImage, mask: NativeImage?): Result<NativeImage> {
        val maskPalette: IntList = ColorUtil.getPaletteFromImage(mask!!)
        if (ColorUtil.getPaletteFromImage(target).intStream().noneMatch(maskPalette::contains)) {
            return Result.fail(Exception("Cannot mask, image does not contain any colors from mask."))
        }
        val outImage = NativeImage(target.width, target.height, false)
        for (x in 0 until target.width) {
            for (y in 0 until target.height) {
                var pixelColor: Int
                pixelColor = if (maskPalette.contains(target.getPixelColor(x, y))) {
                    0x00000000
                } else {
                    target.getPixelColor(x, y)
                }
                outImage.setPixelColor(x, y, pixelColor)
            }
        }
        return Result.success(outImage)
    }

    fun imageMask(target: NativeImage, mask: NativeImage): NativeImage {
        val outImage = NativeImage(target.width, target.height, false)
        for (x in 0 until target.width) {
            for (y in 0 until target.height) {
                var pixelColor: Int
                pixelColor = if (mask.getPixelColor(x, y) != 0x00000000) {
                    0x00000000
                } else {
                    target.getPixelColor(x, y)
                }
                outImage.setPixelColor(x, y, pixelColor)
            }
        }
        return outImage
    }

    fun imageInvertedMask(target: NativeImage, mask: NativeImage): NativeImage {
        val outImage = NativeImage(target.width, target.height, false)
        for (x in 0 until target.width) {
            for (y in 0 until target.height) {
                var pixelColor: Int
                pixelColor = if (mask.getPixelColor(x, y) != 0x00000000) {
                    target.getPixelColor(x, y)
                } else {
                    0x00000000
                }
                outImage.setPixelColor(x, y, pixelColor)
            }
        }
        return outImage
    }

    // fixme: this can cause "debris" pixels?
    fun imageSplitter(image: NativeImage, func: LinearFunction): Result<Pair<NativeImage, NativeImage>> {
        val newImageLeft = NativeImage(image.width, image.height, false)
        val newImageRight = NativeImage(image.width, image.height, false)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val pixel = image.getPixelColor(x, y)
                if (y < func.apply(x.toDouble())) {
                    newImageLeft.setPixelColor(x, y, pixel)
                } else if (y.toDouble() == func.apply(x.toDouble())) {
                    newImageLeft.setPixelColor(x, y, pixel)
                    newImageRight.setPixelColor(x, y, pixel)
                } else {
                    newImageRight.setPixelColor(x, y, pixel)
                }
            }
        }
        return Result.success(Pair(newImageLeft, newImageRight))
    }

    @Throws(IOException::class)
    fun model_generator(item: Item) {
        val file = File("../src/main/resources/assets/modulus/models/item/$item.json")
        try {
            file.createNewFile()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val input = """ {
  "parent": "item/handheld",
  "textures": {
    "layer0": "modulus:item/$item"
  }
}
"""
        Files.writeString(file.toPath(), input)
    }

    @Throws(IOException::class)
    fun model_generator(item: Item, suffix: String) {
        val file = File("../src/main/resources/assets/modulus/models/item/$item.json")
        try {
            file.createNewFile()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val input = """ {
  "parent": "item/handheld",
  "textures": {
    "layer0": "modulus:item/$item$suffix"
  }
}
"""
        Files.writeString(file.toPath(), input)
    }

    fun interface LinearFunction {
        fun apply(x: Double): Double
    }
}
