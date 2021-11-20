package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.graphics.Color

object ContrastBrightnessFilter {

    fun apply(source: Bitmap, brightnessValue:Int, contrast:Int): Bitmap {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        var alpha = (255+contrast)/(255-contrast)
        var u_mean = calculateBrightness(source)

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = Color.red(pixels[index])
                G = Color.green(pixels[index])
                B = Color.blue(pixels[index])

                pixels[index] = Color.rgb(
                    checkBounds(alpha*(R - u_mean) + u_mean + brightnessValue),
                    checkBounds(alpha*(G - u_mean) + u_mean + brightnessValue),
                    checkBounds(alpha*(B - u_mean) + u_mean + brightnessValue))

            }
        }

        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

    fun checkBounds(colorValue: Int):Int{
        return Math.max(Math.min(colorValue, 255),0)
    }

    fun calculateBrightnessEstimate(bitmap: Bitmap, pixelSpacing: Int): Int {
        var R = 0
        var G = 0
        var B = 0
        val height = bitmap.height
        val width = bitmap.width
        var n = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 0
        while (i < pixels.size) {
            val color = pixels[i]
            R += Color.red(color)
            G += Color.green(color)
            B += Color.blue(color)
            n++
            i += pixelSpacing
        }
        return (R + B + G) / (n * 3)
    }

    fun calculateBrightness(bitmap: Bitmap): Int {
        return calculateBrightnessEstimate(bitmap, 1)
    }
}
