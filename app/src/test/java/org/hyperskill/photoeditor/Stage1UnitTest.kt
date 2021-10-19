package org.hyperskill.photoeditor

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowActivity
import org.hyperskill.photoeditor.R.drawable.myexample

import org.junit.Before
import org.robolectric.Shadows.shadowOf
import java.lang.Exception


@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Before
    fun setup() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
    }

    @Test
    fun testShouldCheckImageViewExist() {
        val activity = activityController.setup().get()
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val message = "does view with id \"ivPhoto\" placed in activity?"

        assertNotNull(message, ivPhoto)
    }

    @Test
    fun testShouldCheckImageViewImageNotEmpty() {
        val activity = activityController.setup().get()
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val drawable = (ivPhoto.drawable)
        val message2 = "is \"ivPhoto\" empty?"

        assertNotNull(message2, drawable)
    }

    @Test
    fun testShouldCheckButtonExist() {
        val activity = activityController.setup().get()
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)

        val message = "does view with id \"btnGalllery\" placed in activity?"
        assertNotNull(message, btnGallery)
    }

    @Test
    fun testShouldCheckButtonOpensGallery() {
        val activity = activityController.setup().get()
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        btnGallery.performClick()

        // The intent we expect to be launched when a user clicks on the button
        val expectedIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // An Android "Activity" doesn't expose a way to find out about activities it launches
        // Robolectric's "ShadowActivity" keeps track of all launched activities and exposes this information
        // through the "getNextStartedActivity" method.
        val shadowActivity: ShadowActivity = Shadows.shadowOf(activity)
        val actualIntent = shadowActivity.nextStartedActivity

        // Determine if two intents are the same for the purposes of intent resolution (filtering).
        // That is, if their action, data, type, class, and categories are the same. This does
        // not compare any extra data included in the intents
        assertTrue(actualIntent.filterEquals(expectedIntent))
    }

    @Test
    fun testShouldCheckButtonLoadsImage() {
        val activity = activityController.setup().get()
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        btnGallery.performClick()

        // An Android "Activity" doesn't expose a way to find out about activities it launches
        // Robolectric's "ShadowActivity" keeps track of all launched activities and exposes this information
        // through the "getNextStartedActivity" method.
        val shadowActivity: ShadowActivity = Shadows.shadowOf(activity)

        // Determine if two intents are the same for the purposes of intent resolution (filtering).
        // That is, if their action, data, type, class, and categories are the same. This does
        // not compare any extra data included in the intents

        val activityResult = createGalleryPickActivityResultStub2()

        val intent = shadowActivity!!.peekNextStartedActivityForResult().intent

        shadowOf(activity).receiveResult(
            intent,
            Activity.RESULT_OK,
            activityResult)


        //launcher.dispatchResult(Activity.RESULT_OK, Activity.RESULT_OK, activityResult)
        assertNotNull(ivPhoto.drawable)
        assertEquals(R.drawable.myexample, Shadows.shadowOf(ivPhoto.getDrawable()).getCreatedFromResId())

        //assertEquals(intent.data, activityResult.data)

    }

    private fun createGalleryPickActivityResultStub2(): Intent {
        val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.myexample))
            .appendPath(resources.getResourceTypeName(R.drawable.myexample))
            .appendPath(resources.getResourceEntryName(R.drawable.myexample))
            .build()
        val resultIntent = Intent()
        resultIntent.setData(imageUri)
        return resultIntent
    }

}