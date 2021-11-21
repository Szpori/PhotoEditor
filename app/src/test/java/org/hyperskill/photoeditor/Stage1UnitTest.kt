package org.hyperskill.photoeditor

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowActivity
import org.robolectric.Shadows.shadowOf


@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()

    @Test
    fun testShouldCheckImageViewExist() {
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val message = "does view with id \"ivPhoto\" placed in activity?"

        assertNotNull(message, ivPhoto)
    }

    @Test
    fun testShouldCheckImageViewImageNotEmpty() {
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val drawable = (ivPhoto.drawable)
        val message2 = "is \"ivPhoto\" not empty?"

        assertNotNull(message2, drawable)
    }

    @Test
    fun testShouldCheckButtonExist() {
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        val message = "does view with id \"btnGalllery\" placed in activity?"
        assertNotNull(message, btnGallery)
    }

    @Test
    fun testShouldCheckButtonOpensGallery() {
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
        val btnGallery = activity.findViewById<Button>(R.id.btnGallery)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        btnGallery.performClick()

        val shadowActivity: ShadowActivity = Shadows.shadowOf(activity)

        val activityResult = createGalleryPickActivityResultStub(activity)

        val intent = shadowActivity!!.peekNextStartedActivityForResult().intent

        shadowOf(activity).receiveResult(
            intent,
            Activity.RESULT_OK,
            activityResult)


        assertNotNull(ivPhoto.drawable)
        assertEquals(R.drawable.myexample, shadowOf(ivPhoto.getDrawable()).getCreatedFromResId())
    }

    fun createGalleryPickActivityResultStub(activity: MainActivity): Intent {
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