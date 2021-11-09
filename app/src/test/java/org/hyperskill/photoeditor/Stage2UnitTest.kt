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
import android.widget.Button
import android.widget.ImageView
<<<<<<< Updated upstream
import androidx.test.core.app.ApplicationProvider
=======
>>>>>>> Stashed changes
import com.google.android.material.slider.Slider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowActivity
import java.io.File
import java.net.URL
import android.graphics.BitmapFactory
<<<<<<< Updated upstream
=======
import androidx.test.platform.app.InstrumentationRegistry
>>>>>>> Stashed changes
import org.hamcrest.CoreMatchers.notNullValue


@RunWith(RobolectricTestRunner::class)
class Stage2UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Test
    fun testShouldCheckSliderExist() {
        val activity = activityController.setup().get()
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
<<<<<<< Updated upstream
    fun testShouldCheckSliderWorkingWithoutImage() {
        val activity = activityController.setup().get()
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)

        val message = "does view with id \"slBrightness\" placed in activity?"
        assertNotNull(message, slBrightness)

        slBrightness.value += slBrightness.stepSize

        val drawable = (ivPhoto.drawable)
        val message2 = "is \"ivPhoto\" empty and no crash occurs while swiping slider?"
        assertNull(message2, drawable)
    }


    @Test
    fun testShouldCheckSliderWorkingWithImage() {
        val activity = activityController.setup().get()
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)

        var img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap

        //var img0 = getBitmapFromPath(this, "myexample.jpg")
        //img0?.let { pureBlack(it) }?.let { assertTrue(it) }

        val RGB0 = img0?.let { singleColor(it) }

        slBrightness.value += slBrightness.stepSize*2

        val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val RGB2 = singleColor(img2)



        val message2 = "val1 ${RGB0} val2 ${RGB2}"
        print(message2)
        //assertEquals(img1Hash, img10Hash)
        if (RGB0 != null) {
            assertEquals(message2,RGB0.first+slBrightness.stepSize*2, RGB2.first.toFloat())
        }
    }

    fun resourceToUri(context: Context, resID: Int): Uri? {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.resources.getResourcePackageName(resID) + '/' +
                    context.resources.getResourceTypeName(resID) + '/' +
                    context.resources.getResourceEntryName(resID)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testGetDrawable_rainbow() {
        assertNotNull(
            ApplicationProvider.getApplicationContext<Context>().resources.getDrawable(R.drawable.myexample)
        )
    }

    private fun createGalleryPickActivityResultStub2(activity: MainActivity): Intent {
        //val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
        val resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.myexample2))
            .appendPath(resources.getResourceTypeName(R.drawable.myexample2))
            .appendPath(resources.getResourceEntryName(R.drawable.myexample2))
            .build()
        val resultIntent = Intent()
        //val uri = getUriToDrawable(activity,R.drawable.myexample2)
        resultIntent.setData(imageUri)
        return resultIntent
    }

    fun pureBlack(source: Bitmap): Boolean {
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

               if(R != 0) return false
                if(G != 0) return false
                if(B != 0) return false

            }
        }

        return true
=======
    fun testShouldCheckSliderNotCrashingByDefault() {
        val activity = activityController.setup().get()
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
        val activity = activityController.setup().get()
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val message = "is defaultBitmap set correctly?"
        val bitmap = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        assertEquals(message, singleColor(bitmap), singleColor(createBitmap()))
    }

    @Test
    fun testShouldCheckDefaultBitmapEdit() {
        val activity = activityController.setup().get()
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val bitmap = createBitmap()
        var img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        var RGB0 = img0?.let { singleColor(it) }
        slBrightness.value += slBrightness.stepSize*2
        slBrightness.value += slBrightness.stepSize
        val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val RGB2 = singleColor(img2)
        val message2 = "val0 ${RGB0} val2 ${RGB2}"
        if (RGB0 != null) {
            assertEquals(message2,singleColor(bitmap).first+30, RGB2.first)
            assertEquals(message2,singleColor(bitmap).second+30, RGB2.second)
            assertEquals(message2,singleColor(bitmap).third+30, RGB2.third)
        }

        slBrightness.value -= slBrightness.stepSize*3
    }


    @Test
    fun testShouldCheckNewBitmapEdit() {
        val activity = activityController.setup().get()
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

        for (i in 1..3) {
            slBrightness.value += slBrightness.stepSize
            val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
            val RGB2 = singleColor(img2)
            val message2 = "val0 ${RGB0} val2 ${RGB2}"
            if (RGB0 != null) {
                assertEquals(message2,changeBrightness(RGB0.first,slBrightness.stepSize*i.toDouble()), RGB2.first)
                assertEquals(message2,changeBrightness(RGB0.second,slBrightness.stepSize*i.toDouble()), RGB2.second)
                assertEquals(message2,changeBrightness(RGB0.third,slBrightness.stepSize*i.toDouble()), RGB2.third)
            }
        }

        img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        RGB0 = img0?.let { singleColor(it) }


        for (i in 1..3) {
            slBrightness.value -= slBrightness.stepSize
            val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
            val RGB2 = singleColor(img2)
            val message2 = "val0 ${RGB0} val2 ${RGB2}"
            if (RGB0 != null) {
                assertEquals(message2,changeBrightness(RGB0.first,-slBrightness.stepSize*i.toDouble()), RGB2.first)
                assertEquals(message2,changeBrightness(RGB0.second,-slBrightness.stepSize*i.toDouble()), RGB2.second)
                assertEquals(message2,changeBrightness(RGB0.third,-slBrightness.stepSize*i.toDouble()), RGB2.third)
            }
        }



>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        var x = 80
=======
        var x = 90
>>>>>>> Stashed changes

        index = y * width + x
        // get color
        R = Color.red(pixels[index])
        G = Color.green(pixels[index])
        B = Color.blue(pixels[index])

        return  Triple(R,G,B)
    }

<<<<<<< Updated upstream

    private fun getBitmapFromPath(obj: Any, fileName: String): Bitmap? {
        val classLoader = obj.javaClass.classLoader
        val resource: URL = classLoader!!.getResource(fileName)
        val bitmap = BitmapFactory.decodeFile(resource.getPath())
        return bitmap
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun fileObjectShouldNotBeNull() {
        val bitmap = getBitmapFromPath(this, "myexample.jpg")
        assertThat(bitmap, notNullValue())


    }

=======
    fun createBitmap(): Bitmap {
        val width = 100
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
>>>>>>> Stashed changes
}