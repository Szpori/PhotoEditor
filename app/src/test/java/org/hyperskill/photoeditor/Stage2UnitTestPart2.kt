package org.hyperskill.photoeditor

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
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
import androidx.test.platform.app.InstrumentationRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowActivity


@RunWith(RobolectricTestRunner::class)
class Stage2UnitTestPart2 {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()

    @Test
    fun testShouldCheckNewBitmapEdit() {
        var defaultBitmap = createBitmap()
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        btnGallery.performClick()
        val shadowActivity: ShadowActivity = Shadows.shadowOf(activity)
        // Determine if two intents are the same for the purposes of intent resolution (filtering).
        // That is, if their action, data, type, class, and categories are the same. This does
        // not compare any extra data included in the intents
        val activityResult = createGalleryPickActivityResultStub2(activity)
        val intent = shadowActivity!!.peekNextStartedActivityForResult().intent
        Shadows.shadowOf(activity).receiveResult(
            intent,
            Activity.RESULT_OK,
            activityResult)

        var img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        var RGB0 = img0?.let { singleColor(it) }

        for (i in 1..1) {
            slBrightness.value += slBrightness.stepSize
            val model = runBlocking() {
                val model = mockk<BrightnessFilter>(relaxed = true)
                //val model = BrightnessFilter(slBrightness, ivPhoto, coroutineContext)
                model.setBrightness(defaultBitmap, slBrightness.value.toInt())
                model
            }
            shadowOf(getMainLooper()).idle()

            val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
            val RGB2 = singleColor(img2)
            val message2 = "val0 ${RGB0} val2 ${RGB2}"
            if (RGB0 != null) {
                assertEquals(message2,changeBrightness(RGB0.first,slBrightness.stepSize*i.toDouble()), RGB2.first)
                assertEquals(message2,changeBrightness(RGB0.second,slBrightness.stepSize*i.toDouble()), RGB2.second)
                assertEquals(message2,changeBrightness(RGB0.third,slBrightness.stepSize*i.toDouble()), RGB2.third)
            }
        }
    }



    fun singleColor(source: Bitmap): Triple<Int, Int, Int> {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var index: Int
        var R: Int
        var G: Int
        var B: Int
        var y = 80
        var x = 90

        index = y * width + x
        // get color
        R = Color.red(pixels[index])
        G = Color.green(pixels[index])
        B = Color.blue(pixels[index])

        return  Triple(R,G,B)
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
                R = x % 100
                G = y % 100
                B = (x+y) % 100

                pixels[index] = Color.rgb(R,G,B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

    fun changeBrightness(colorValue:Int, filterValue:Double):Int {
        return Math.max(Math.min(colorValue + filterValue, 255.0),0.0).toInt()
    }

    private fun createGalleryPickActivityResultStub2(activity: MainActivity): Intent {
        val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.myexample))
            .appendPath(resources.getResourceTypeName(R.drawable.myexample))
            .appendPath(resources.getResourceEntryName(R.drawable.myexample))
            .build()
        val resultIntent = Intent()
        val uri = getUriToDrawable(activity,R.drawable.myexample)
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