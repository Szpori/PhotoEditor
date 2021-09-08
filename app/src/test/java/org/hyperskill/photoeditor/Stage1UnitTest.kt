package org.hyperskill.photoeditor

import android.widget.Button
import android.widget.ImageView
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest {

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

}