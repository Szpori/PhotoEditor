package org.hyperskill.photoeditor


import android.graphics.BitmapFactory
import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import android.widget.Button
import org.junit.Assert.*
import org.robolectric.Shadows.shadowOf
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


@RunWith(RobolectricTestRunner::class)
class Stage3UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()

    /*

    @Test
    fun testShouldCheckSaveFunIsCalled() {
        val btnSave = activity.findViewById<Button>(R.id.btnSave)
        val btnHandler = mockk<AbstractSaveButtonHandler>()
        every { btnHandler.saveImage() } returns Uri.parse(R.drawable.myexample.toString())
        val buttonExecutor = ButtonExecutor(btnHandler, btnSave)
        buttonExecutor.saveButton.performClick()
        verify { btnHandler.saveImage() }
    }

     */

    /*
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
    */

    @Test
    fun testShouldCheckSomeNewBitmapIsCreated() {
        val btnSave = activity.findViewById<Button>(R.id.btnSave)
        val cr = activity.contentResolver
        val output = ByteArrayOutputStream()
        val crs = shadowOf(cr)
        val uri = Uri.parse("content://media/external/images/media/1")
        crs.registerOutputStream(uri, output)
        btnSave.performClick()
        crs.registerInputStream(uri, ByteArrayInputStream(output.toByteArray()))
        val bitmap = cr.openInputStream(uri!!).use(BitmapFactory::decodeStream)!!
        assertEquals(200, bitmap.width)
    }

}