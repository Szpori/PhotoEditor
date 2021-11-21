package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.google.android.material.slider.Slider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import android.os.Looper.getMainLooper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import org.robolectric.Shadows.shadowOf


@RunWith(RobolectricTestRunner::class)
class Stage6UnitTestPart2 {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()

    val marginError = 3

    @Test
    fun testShouldCheckDefaultBitmapEditCoroutine() {
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val slContrast = activity.findViewById<Slider>(R.id.slContrast)
        val slSaturation = activity.findViewById<Slider>(R.id.slSaturation)
        val slGamma = activity.findViewById<Slider>(R.id.slGamma)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)

        var img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        var RGB0 = img0?.let { singleColor(it,80, 90) }
        slBrightness.value += slBrightness.stepSize
        slContrast.value += slContrast.stepSize*4
        slContrast.value += slContrast.stepSize
        slSaturation.value += slSaturation.stepSize*10
        slSaturation.value += slSaturation.stepSize*5
        slGamma.value += slGamma.stepSize*10

        val model = runBlocking() {
            val model = mockk<IFilterApplier>(relaxed = true)
            //val model = BrightnessFilter(slBrightness, ivPhoto, coroutineContext)
            model.setBrightness(img0, slBrightness.value.toInt(), slContrast.value.toInt(), slSaturation.value.toInt(), slGamma.value.toInt())
            model
        }
        shadowOf(getMainLooper()).idle()

        val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val RGB2 = singleColor(img2, 60, 70)
        val message2 = "val0 ${RGB0} val2 ${RGB2}"

        if (RGB0 != null) {
            assertTrue(message2,Math.abs(12-RGB2.first) <= marginError)
            assertTrue(message2,Math.abs(95-RGB2.second) <= marginError)
            assertTrue(message2,Math.abs(150-RGB2.third) <= marginError)
        }
    }


    fun singleColor(source: Bitmap, x0: Int = 60, y0: Int = 70): Triple<Int, Int, Int> {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var index: Int
        var R: Int
        var G: Int
        var B: Int
        var y = x0
        var x = y0

        index = y * width + x
        // get color
        R = Color.red(pixels[index])
        G = Color.green(pixels[index])
        B = Color.blue(pixels[index])

        return Triple(R, G, B)
    }

    fun createBitmap(): Bitmap {
        val width = 200
        val height = 100
        val pixels = IntArray(width * height)
        // get pixel array from source

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = x % 100 + 40
                G = y % 100 + 80
                B = (x+y) % 100 + 120

                pixels[index] = Color.rgb(R,G,B)

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