package org.hyperskill.photoeditor


import android.content.ContentResolver
import android.content.Context
import io.mockk.verify
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.widget.ImageView
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import android.widget.Button
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.*
import org.robolectric.Shadows.shadowOf
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream


@RunWith(RobolectricTestRunner::class)
class Stage3UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()


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



    @Test
    fun testShouldCheckSaveFunIsCalled() {
        val btnSave = activity.findViewById<Button>(R.id.btnSave)
        val iv = ImageView(activity).also { it.setImageResource(R.drawable.myexample) }
        val btnHandler = spyk(ButtonHandler(iv, activity.contentResolver))
        val buttonExecutor = ButtonExecutor(btnHandler, btnSave)
        buttonExecutor.saveButton.performClick()
        verify {
            btnHandler.saveImage()
            btnHandler.saveBitmap(any(), any(), any())
        }
    }

    @Test
    fun testShouldCheckBitmapProperlySaved() {
        val outputStream = mockk<OutputStream>()
        val bitmap = mockk<Bitmap>(relaxed = true)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val btnHandler = ButtonHandler(ivPhoto, activity.contentResolver)
        btnHandler.saveBitmap(bitmap, outputStream, 100)
        verify { (bitmap).compress(Bitmap.CompressFormat.JPEG, 100, outputStream) }
        // good test imo
    }

    @Test
    fun testShouldCheckSomeNewBitmapIsCreated() {
        val cr = activity.contentResolver
        val output = ByteArrayOutputStream()
        val crs = shadowOf(cr)
        val expectedUri = Uri.parse("content://media/external/images/media/1")
        crs.registerOutputStream(expectedUri, output)
        val btnHandler = ButtonHandler(activity.findViewById(R.id.ivPhoto), cr)
        val uri = btnHandler.saveImage()
        crs.registerInputStream(expectedUri, ByteArrayInputStream(output.toByteArray()))
        val bitmap = cr.openInputStream(uri!!).use(BitmapFactory::decodeStream)!!
        assertEquals(expectedUri, uri)
        assertEquals(200, bitmap.width)
    }

}