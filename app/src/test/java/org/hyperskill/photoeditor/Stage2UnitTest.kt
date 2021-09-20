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
import android.support.annotation.AnyRes
import android.support.annotation.NonNull
import android.widget.Button
import android.widget.ImageView
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.slider.Slider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowActivity


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


        //launcher.dispatchResult(Activity.RESULT_OK, Activity.RESULT_OK, activityResult)
        assertNotNull(ivPhoto.drawable)
        assertEquals(R.drawable.myexample2, Shadows.shadowOf(ivPhoto.getDrawable()).getCreatedFromResId())

        val img0 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val RGB0 = singleColor(img0)

        slBrightness.value += slBrightness.stepSize*2

        val img2 = (ivPhoto.getDrawable() as BitmapDrawable).bitmap
        val RGB2 = singleColor(img2)


        val message2 = "val1 ${RGB0} val2 ${RGB2}"
        print(message2)
        //assertEquals(img1Hash, img10Hash)
        assertEquals(message2,RGB0.first+slBrightness.stepSize*2, RGB2.first.toFloat())
        assertEquals(message2,RGB0.second+slBrightness.stepSize*2, RGB2.second.toFloat())
        assertEquals(message2,RGB0.third+slBrightness.stepSize*2, RGB2.third.toFloat())

        assertNotEquals(message2,RGB0.first+slBrightness.stepSize*2, RGB2.first.toFloat())
    }

    @Test
    @Throws(Exception::class)
    fun testGetDrawable_rainbow() {
        assertNotNull(1
            //ApplicationProvider.getApplicationContext<Context>().resources.getDrawable(R.drawable.download)
        )
    }

    private fun createGalleryPickActivityResultStub2(activity: MainActivity): Intent {
        val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.myexample2))
            .appendPath(resources.getResourceTypeName(R.drawable.myexample2))
            .appendPath(resources.getResourceEntryName(R.drawable.myexample2))
            .build()
        val resultIntent = Intent()
        val uri = getUriToDrawable(activity,R.drawable.myexample2)
        resultIntent.setData(uri)
        return resultIntent
    }

    fun getUriToDrawable(
        @NonNull context: Context,
        @AnyRes drawableId: Int
    ): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + context.getResources().getResourcePackageName(drawableId)
                    + '/' + context.getResources().getResourceTypeName(drawableId)
                    + '/' + context.getResources().getResourceEntryName(drawableId)
        )
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
        var y = 120
        var x = 120

        index = y * width + x
        // get color
        R = Color.green(pixels[index])
        G = Color.green(pixels[index])
        B = Color.green(pixels[index])

        return  Triple( R, G, B)
    }

}