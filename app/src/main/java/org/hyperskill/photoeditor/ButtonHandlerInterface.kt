package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.net.Uri
import java.io.OutputStream

interface ButtonHandlerInterface {

    fun saveImage(): Uri

    fun saveBitmap(bitmap: Bitmap, stream: OutputStream, quality:Int)

}