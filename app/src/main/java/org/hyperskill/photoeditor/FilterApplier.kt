package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class FilterApplier(
    override val selectedImage: ImageView,
    override val parentContext: CoroutineContext
) :IFilterApplier {
    private val modelScope = CoroutineScope(parentContext)
    override suspend fun applyFilterChange(defaultImageBitMap:Bitmap, brightnessValue:Int, contrast:Int, saturation:Int, gamma:Int) {
        val filteredDeferred = modelScope.async(Dispatchers.Default) {
           apply(defaultImageBitMap, brightnessValue, contrast, saturation, gamma)
        }
        val filteredBitmap = filteredDeferred.await()
        loadImage(filteredBitmap)
    }

    private fun loadImage(bmp: Bitmap) {
        selectedImage.setImageBitmap(bmp)
    }

    fun apply(source: Bitmap, brightnessValue:Int, contrast:Int, saturation:Int, gamma:Int): Bitmap {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        var alpha1 = (255+contrast)/(255-contrast)
        var alpha2 = (255+saturation)/(255-saturation)
        var u_mean = calculateBrightness(source)

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = Color.red(pixels[index])
                G = Color.green(pixels[index])
                B = Color.blue(pixels[index])



                val u = (R+G+B)/3

                R = checkBounds((alpha1*(R - u_mean) + u_mean) + brightnessValue)
                G = checkBounds((alpha1*(G - u_mean) + u_mean) + brightnessValue)
                B = checkBounds((alpha1*(B - u_mean) + u_mean) + brightnessValue)

                R = checkBounds((alpha2*(R - u) + u))
                G = checkBounds((alpha2*(G - u) + u))
                B = checkBounds((alpha2*(B - u) + u))


                R = (255 * Math.pow(R/255.0, gamma.toDouble())).toInt()
                G = (255 * Math.pow(G/255.0, gamma.toDouble())).toInt()
                B = (255 * Math.pow(B/255.0, gamma.toDouble())).toInt()


                pixels[index] = Color.rgb(R,G,B)
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