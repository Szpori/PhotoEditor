package org.hyperskill.photoeditor

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import java.io.OutputStream


class ButtonHandler(selectedImage: ImageView, contentResolver: ContentResolver
) {

    val selectedImage = selectedImage
    val contentResolver = contentResolver

    fun saveImage(): Uri? {
        val bitmap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        //val uri = Uri.parse("Bad uri")
        val uri = contentResolver.insert(contentUri, ContentValues())
        val stream = uri?.let { contentResolver.openOutputStream(it) }
        saveBitmap(bitmap, stream!!, 100)
        return uri
    }

    fun saveBitmap(bitmap: Bitmap, stream: OutputStream, quality:Int) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    }


}