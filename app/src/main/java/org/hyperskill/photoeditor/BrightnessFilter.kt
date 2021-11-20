package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class BrightnessFilter(
    override val selectedImage: ImageView,
    override val parentContext: CoroutineContext
) :IBrightnessFilter {

    private val modelScope = CoroutineScope(parentContext)

    override suspend fun setBrightness(defaultImageBitMap:Bitmap, brightnessValue:Int) {
        val filteredDeferred = modelScope.async(Dispatchers.Default) {
           apply(defaultImageBitMap, brightnessValue)
        }
        val filteredBitmap = filteredDeferred.await()
        loadImage(filteredBitmap)
    }

    private fun loadImage(bmp: Bitmap) {
        selectedImage.setImageBitmap(bmp)
    }

    fun apply(source: Bitmap, brightnessValue:Int): Bitmap {
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
                    changeBrightness(R, brightnessValue),
                    changeBrightness(G, brightnessValue),
                    changeBrightness(B, brightnessValue))
            }
        }

        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

    fun changeBrightness(colorValue:Int, filterValue:Int):Int {
        return Math.max(Math.min(colorValue + filterValue, 255),0)
    }
}