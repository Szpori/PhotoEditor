package org.hyperskill.photoeditor

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.slider.Slider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Test
    fun testShouldCheckImageViewExist() {
        val activity = activityController.setup().get()
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val message = "does view with id \"ivPhoto\" placed in activity?"

        assertNotNull(message, ivPhoto)

    }

    @Test
    fun testShouldCheckImageViewImageEmpty() {
        val activity = activityController.setup().get()
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val drawable = (ivPhoto.drawable)
        val message2 = "is \"ivPhoto\" empty?"

        assertNull(message2, drawable)
    }

    @Test
    fun testShouldCheckButtonExist() {
        val activity = activityController.setup().get()
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)

        val message = "does view with id \"btnGalllery\" placed in activity?"
        assertNotNull(message, btnGallery)
    }

    @Test
    fun testShouldCheckSliderExist() {
        val activity = activityController.setup().get()
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)

        val message = "does view with id \"slBrightness\" placed in activity?"
        assertNotNull(message, slBrightness)

        val message2 = "\"slider\" should have proper stepSize attribute"
        assertEquals(message2, slBrightness.stepSize, 0.1f)

        val message3 = "\"slider\" should have proper valueFrom attribute"
        assertEquals(message3, slBrightness.valueFrom, 0.2f)

        val message4 = "\"slider\" should have proper valueTo attribute"
        assertEquals(message4, slBrightness.valueTo, 1.8f)

        val message5 = "\"slider\" should have proper initial value"
        assertEquals(message5, 1.0f, slBrightness.value)
    }

    @Test
    fun testShouldCheckSliderWorkingWithoutImage() {
        val activity = activityController.setup().get()
        val slBrightness = activity.findViewById<Slider>(R.id.slBrightness)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)

        val message = "does view with id \"slBrightness\" placed in activity?"
        assertNotNull(message, slBrightness)

        slBrightness.value = 1.2f

        val drawable = (ivPhoto.drawable)
        val message2 = "is \"ivPhoto\" empty and no crash occurs while swiping slider?"
        assertNull(message2, drawable)

    }

    @Test
    fun testShouldCheckButtonOnClick() {
        val activity = activityController.setup().get()
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        btnGallery.callOnClick()


    }

}