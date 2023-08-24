package lol.j0.modulus

import com.mojang.blaze3d.texture.NativeImage
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.Range
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.min

/*
* Copyright (c) 2021-2022 LambdAurora <email@lambdaurora.dev>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * Utilities for color manipulation.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
class ColorUtil private constructor() {
    init {
        throw UnsupportedOperationException("ColorUtil only contains static definitions.")
    }

    companion object {
        const val BLACK = -0x1000000
        const val WHITE = -0x1
        const val TEXT_COLOR = -0x1f1f20
        const val UNEDITABLE_COLOR = -0x8f8f90

        /**
         * Returns a color value between `0.0` and `1.0` using the integer value.
         *
         * @param colorComponent the color value as int
         * @return the color value as float
         */
        fun floatColor(colorComponent: @Range(from = 0, to = 255) Int): Float {
            return colorComponent / 255f
        }

        /**
         * Returns a color value between `0` and `255` using the float value.
         *
         * @param colorComponent the color value as float
         * @return the color value as integer
         */
        fun intColor(colorComponent: Float): @Range(from = 0, to = 255) Int {
            return MathHelper.clamp((colorComponent * 255f).toInt(), 0, 255)
        }

        /**
         * Packs the given color into an ARGB integer.
         *
         * @param red the red color value
         * @param green the green color value
         * @param blue the blue color value
         * @param alpha the alpha value
         * @return the packed ARGB color
         */
        fun packARGBColor(red: @Range(from = 0, to = 255) Int, green: @Range(from = 0, to = 255) Int, blue: @Range(from = 0, to = 255) Int, alpha: @Range(from = 0, to = 255) Int): Int {
            return (alpha and 255 shl 24) + (red and 255 shl 16) + (green and 255 shl 8) + (blue and 255)
        }

        /**
         * Unpacks the given ARGB color into an array of 4 integers in the following format: `{red, green, blue, alpha}`.
         *
         * @param color the ARGB color
         * @return the 4 color components as a RGBA array
         */
        fun unpackARGBColor(color: Int): IntArray {
            return intArrayOf(
                    argbUnpackRed(color),
                    argbUnpackGreen(color),
                    argbUnpackBlue(color),
                    argbUnpackAlpha(color)
            )
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

        fun luminance(color: Int): Float {
            return luminance(argbUnpackRed(color), argbUnpackGreen(color), argbUnpackBlue(color))
        }

        fun luminance(red: Int, green: Int, blue: Int): Float {
            return 0.2126f * red + 0.7152f * green + 0.0722f * blue
        }

        fun blendColors(foreground: Int, background: Int): Int {
            val _alpha = floatColor(argbUnpackAlpha(foreground))
            val beta = floatColor(argbUnpackAlpha(background)) * (1 - _alpha)
            val alpha = _alpha + beta
            return packARGBColor(
                    intColor((floatColor(argbUnpackRed(foreground)) * _alpha + floatColor(argbUnpackRed(background)) * beta) / alpha),
                    intColor((floatColor(argbUnpackGreen(foreground)) * _alpha + floatColor(argbUnpackGreen(background)) * beta) / alpha),
                    intColor((floatColor(argbUnpackBlue(foreground)) * _alpha + floatColor(argbUnpackBlue(background)) * beta) / alpha),
                    intColor(alpha))
        }

        fun mixColors(a: Int, b: Int, ratio: Float): Int {
            val aA = unpackARGBColor(a)
            val bA = unpackARGBColor(b)
            val r = IntArray(4)
            for (i in 0..3) {
                r[i] = intColor(floatColor(aA[i]) * (1 - ratio) + floatColor(bA[i]) * ratio)
            }
            return packARGBColor(r[0], r[1], r[2], r[3])
        }

        /**
         * Multiples two ARGB color.
         *
         * @param a an ARGB color
         * @param b an ARGB color
         * @return the multiplied color
         */
        fun argbMultiply(a: Int, b: Int): Int {
            val aRed = floatColor(argbUnpackRed(a))
            val aGreen = floatColor(argbUnpackGreen(a))
            val aBlue = floatColor(argbUnpackBlue(a))
            val aAlpha = floatColor(argbUnpackAlpha(a))
            val bRed = floatColor(argbUnpackRed(b))
            val bGreen = floatColor(argbUnpackGreen(b))
            val bBlue = floatColor(argbUnpackBlue(b))
            val bAlpha = floatColor(argbUnpackAlpha(b))
            return packARGBColor(
                    intColor(aRed * bRed),
                    intColor(aGreen * bGreen),
                    intColor(aBlue * bBlue),
                    intColor(aAlpha * bAlpha)
            )
        }

        fun rgbToHsb(r: Int, g: Int, b: Int): FloatArray {
            val hsb = FloatArray(3)
            var cMax = max(r.toDouble(), g.toDouble()).toInt()
            if (b > cMax) {
                cMax = b
            }
            var cMin = min(r.toDouble(), g.toDouble()).toInt()
            if (b < cMin) {
                cMin = b
            }
            val brightness = cMax.toFloat() / 255f
            val saturation: Float
            saturation = if (cMax != 0) {
                (cMax - cMin).toFloat() / cMax.toFloat()
            } else {
                0f
            }
            var hue: Float
            if (saturation == 0f) {
                hue = 0f
            } else {
                val redC = (cMax - r).toFloat() / (cMax - cMin).toFloat()
                val greenC = (cMax - g).toFloat() / (cMax - cMin).toFloat()
                val blueC = (cMax - b).toFloat() / (cMax - cMin).toFloat()
                hue = if (r == cMax) {
                    blueC - greenC
                } else if (g == cMax) {
                    2f + redC - blueC
                } else {
                    4f + greenC - redC
                }
                hue /= 6f
                if (hue < 0f) {
                    ++hue
                }
            }
            hsb[0] = hue
            hsb[1] = saturation
            hsb[2] = brightness
            return hsb
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
    }
}
