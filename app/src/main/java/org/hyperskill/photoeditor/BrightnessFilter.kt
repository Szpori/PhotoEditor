package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.graphics.Color

object BrightnessFilter {

    fun apply(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = Color.red(pixels[index])
                G = Color.green(pixels[index])
                B = Color.blue(pixels[index])

                pixels[index] = Color.rgb(
                    changeBrightness(R, MainActivity.brightnessValue),
                    changeBrightness(G, MainActivity.brightnessValue),
                    changeBrightness(B, MainActivity.brightnessValue))

            }
        }

        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

    fun changeBrightness(colorValue:Int, filterValue:Double):Int {
        return Math.max(Math.min(colorValue * filterValue, 255.0),0.0).toInt()
    }
}