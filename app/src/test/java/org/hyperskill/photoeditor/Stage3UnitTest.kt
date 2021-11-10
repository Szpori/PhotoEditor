package org.hyperskill.photoeditor


import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
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
import java.io.InputStream
import android.widget.Button
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.*
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import java.io.BufferedInputStream
import java.io.IOException
import java.io.OutputStream
import org.robolectric.shadows.ShadowBitmap
import java.lang.Exception
import android.provider.MediaStore

import org.robolectric.shadows.ShadowBitmapFactory



@RunWith(RobolectricTestRunner::class)
class Stage3UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)
    val activity = activityController.setup().get()

    @Test
    fun decodeResource_shouldGetCorrectColorFromPngImage2() {
        val resources: Resources = activity.getResources()
        val opts = BitmapFactory.Options()
        val uri = getUriToDrawable(activity, R.drawable.myexample)
        val bitmap = decodeStream_shouldSetDescriptionAndCreatedFrom(uri!!)!!
        val shadowBitmap = shadowOf(bitmap)
        assertEquals("Bitmap for ${uri.toString()}", shadowBitmap.description)
        //val bitmap = BitmapFactory.decodeResource(resources, R.drawable.pure_blue, opts)
        println(singleColor(bitmap))
        println(bitmap.height)
        println(shadowBitmap.createdFromHeight)
        println(shadowBitmap.createdFromResId)
        println(bitmap.getPixel(0, 0))
        assertEquals(Color.BLUE,bitmap.getPixel(0, 0))
        assertEquals(Color.RED,bitmap.getPixel(0, 0))
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
        val btnHandler = mockk<ButtonHandler>()
        every { btnHandler.saveImage() } returns Uri.parse(R.drawable.myexample.toString())
        every { btnHandler.saveBitmap(any(), any(), any())} returns Unit
        val buttonExecutor = ButtonExecutor(btnHandler, btnSave)
        buttonExecutor.saveButton.performClick()
        verify { btnHandler.saveImage() }
        //verify { btnHandler.saveBitmap(any(), any(), any()) }
    }


    @Test
    fun testShouldCheckBitmapProperlySaved() {
        val outputStream = mockk<OutputStream>()
        val bitmap = mockk<Bitmap>(relaxed = true)
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val btnHandler = ButtonHandler(ivPhoto, activity.contentResolver)
        btnHandler.saveBitmap(bitmap, outputStream, 100)
        verify { (bitmap).compress(Bitmap.CompressFormat.JPEG, 100, outputStream) }
    }



    @Test
    fun testShouldCheckSomeNewBitmapIsCreated() {
        val ivPhoto = activity.findViewById<ImageView>(R.id.ivPhoto)
        val btnHandler = ButtonHandler(ivPhoto, activity.contentResolver)
        val uri = btnHandler.saveImage()
        assertEquals("content://media/external/images/media/1", uri.toString())
    }


    fun decodeStream_shouldSetDescriptionAndCreatedFrom(uri:Uri): Bitmap? {
        val inputStream: InputStream? =
            activity.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val shadowBitmap = shadowOf(bitmap)
        shadowBitmap.createdFromStream
        return bitmap
    }

    @Test
    fun decodeResourceStream_shouldGetCorrectColorFromPngImage() {
        assertEquals(Color.BLUE, getPngImageColorFromResourceStream("res/drawable/pure_blue.png"))
    }

    private fun getPngImageColorFromResourceStream(pngImagePath: String): Int {
        val bitmap: Bitmap = getBitmapFromResourceStream(pngImagePath)!!
        return bitmap.getPixel(0, 0) ?: Int.MIN_VALUE
    }

    private fun getBitmapFromResourceStream(imagePath: String): Bitmap? {
        try {
            BufferedInputStream(javaClass.classLoader!!.getResourceAsStream(imagePath)).use { inputStream ->
                inputStream.mark(inputStream.available())
                val opts = BitmapFactory.Options()
                val resources: Resources = activity.getResources()
                return BitmapFactory.decodeResourceStream(resources, null, inputStream, null, opts)
            }
        } catch (e: IOException) {
            return null
        }
    }

}