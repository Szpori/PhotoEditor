package org.hyperskill.photoeditor

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import com.google.android.material.slider.Slider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import android.os.Looper.getMainLooper
import android.widget.Button
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowActivity


@RunWith(RobolectricTestRunner::class)
class Stage6UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()

    val marginError = 1

    @Test
    fun testShouldCheckSliderExist() {
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)

        val message = "does view with id \"slBrightness\" placed in activity?"
        assertNotNull(message, slBrightness)

        val message2 = "\"slider\" should have proper stepSize attribute"
        assertEquals(message2, slBrightness.stepSize, 10f)

        val message3 = "\"slider\" should have proper valueFrom attribute"
        assertEquals(message3, slBrightness.valueFrom, -250f)

        val message4 = "\"slider\" should have proper valueTo attribute"
        assertEquals(message4, slBrightness.valueTo, 250f)

        val message5 = "\"slider\" should have proper initial value"
        assertEquals(message5, slBrightness.value, 0f)
    }

    @Test
    fun testShouldCheckSliderNotCrashingByDefault() {
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        slBrightness.value += slBrightness.stepSize
        val bitmap = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val message2 = "is \"ivPhoto\" not empty and no crash occurs while swiping slider?"
        assertNotNull(message2, bitmap)
        slBrightness.value -= slBrightness.stepSize
    }

    @Test
    fun testShouldCheckImageIsSetToDefaultBitmap() {
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val message = "is defaultBitmap set correctly?"
        val bitmap = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val bitmap2 = createBitmap()
        assertEquals(message, singleColor(bitmap), singleColor(bitmap2))
        assertEquals(message, bitmap.width, bitmap2.width)
    }

    @Test
    fun testShouldCheckDefaultBitmapEdit() {
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        var img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        var RGB0 = img0?.let { singleColor(it) }
        slBrightness.value += slBrightness.stepSize
        shadowOf(getMainLooper()).idle()
        val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val RGB2 = singleColor(img2)
        val message2 = "val0 ${RGB0} val2 ${RGB2}"
        if (RGB0 != null) {
            assertFalse(message2, Math.abs(RGB0.first + 10 - RGB2.first) <= marginError)
            assertFalse(message2, Math.abs(RGB0.second + 10 - RGB2.second) <= marginError)
            assertFalse(message2, Math.abs(RGB0.third + 10 - RGB2.third) <= marginError)
        }
        slBrightness.value -= slBrightness.stepSize
    }


    @Test
    fun testShouldCheckNewBitmapEditCoroutine() {
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val slContrast = activity.findViewById<Slider>(R.id.slContrast)
        val slSaturation = activity.findViewById<Slider>(R.id.slSaturation)
        val slGamma = activity.findViewById<Slider>(R.id.slGamma)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        btnGallery.performClick()
        val shadowActivity: ShadowActivity = Shadows.shadowOf(activity)
        // Determine if two intents are the same for the purposes of intent resolution (filtering).
        // That is, if their action, data, type, class, and categories are the same. This does
        // not compare any extra data included in the intents
        val activityResult = createGalleryPickActivityResultStub(activity)
        val intent = shadowActivity!!.peekNextStartedActivityForResult().intent
        Shadows.shadowOf(activity).receiveResult(
            intent,
            Activity.RESULT_OK,
            activityResult)

        var img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        var RGB0 = img0?.let { singleColor(it) }

            val model = runBlocking() {
                slBrightness.value += slBrightness.stepSize*2
                slBrightness.value += slBrightness.stepSize
                val model = mockk<IFilterApplier>(relaxed = true)
                //val model = BrightnessFilter(slBrightness, ivPhoto, coroutineContext)
                //model.setBrightness(defaultBitmap, slBrightness.value.toInt())
                model
            }
            every {
                runBlocking {
                    model.setBrightness(any(),any(),any(),any(),any())
                }
            } returns Unit
            shadowOf(getMainLooper()).idle()

            val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
            val RGB2 = singleColor(img2)
            val message2 = "val0 ${RGB0} val2 ${RGB2}"

            if (RGB0 != null) {
                assertTrue(message2, Math.abs(RGB0.first + slBrightness.stepSize*3 - RGB2.first) <= marginError)
                assertTrue(message2, Math.abs(RGB0.second + slBrightness.stepSize*3 - RGB2.second) <= marginError)
                assertTrue(message2, Math.abs(RGB0.third + slBrightness.stepSize*3 - RGB2.third) <= marginError)
            }

        val sliderExecutor = SliderExecutor(model, slBrightness, slContrast, slSaturation, slGamma, img0)
        sliderExecutor.slBrightness.value +=  sliderExecutor.slBrightness.stepSize
        sliderExecutor.slBrightness.value -=  sliderExecutor.slBrightness.stepSize

        verify {
            runBlocking {
                model.setBrightness(any(),any(), any(), any(), any())
            }
            shadowOf(getMainLooper()).idle()
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
                B = (x + y) % 100 + 120

                pixels[index] = Color.rgb(R, G, B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

    fun changeBrightness(colorValue:Int, filterValue:Int):Int {
        return Math.max(Math.min(colorValue + filterValue, 255),0)
    }

    fun createGalleryPickActivityResultStub(activity: MainActivity): Intent {
        val resultIntent = Intent()
        val uri = getUriToDrawable(activity, R.drawable.myexample)
        resultIntent.setData(uri)
        return resultIntent
    }

    fun getUriToDrawable(
        context: Context,
        drawableId: Int
    ): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + context.getResources().getResourcePackageName(drawableId)
                    + '/' + context.getResources().getResourceTypeName(drawableId)
                    + '/' + context.getResources().getResourceEntryName(drawableId)
        )
    }
}