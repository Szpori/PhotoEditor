package org.hyperskill.photoeditor

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
<<<<<<< Updated upstream
import org.hyperskill.photoeditor.BrightnessFilter.changeBrightness
=======
import java.io.File
import java.io.OutputStream as OutputStream
import android.widget.Button
>>>>>>> Stashed changes


class MainActivity : AppCompatActivity() {

    private lateinit var selectedImage: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var brightnessSlider: Slider
    private lateinit var defaultImageBitMap: Bitmap
    private lateinit var buttonSave: Button
    lateinit var buttonExecutor: ButtonExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        val btnHandler = ButtonHandler(selectedImage, contentResolver)
        buttonExecutor = ButtonExecutor(btnHandler, buttonSave)

        brightnessSlider.addOnChangeListener { slider, value, fromUser ->
            setBrightnessValue()
        }

        resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setPhoto(result)
            }
        }

<<<<<<< Updated upstream


        //val contentUri = resourceToUri(this,R.drawable.myexample)
        //selectedImage!!.setImageURI(contentUri)
        defaultImageBitMap = createBitmap()
        selectedImage!!.setImageBitmap(defaultImageBitMap)
        /*
        val goodImg = (selectedImage.getDrawable() as BitmapDrawable).bitmap
        if((pureBlack(goodImg))) {
            brightnessSlider.value += 100f
        } else {
            brightnessSlider.value -= 100f
        }

         */




        //selectedImage!!.setImageResource(R.drawable.download)
        //defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
    }

    fun resourceToUri(context: Context, resID: Int): Uri? {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.resources.getResourcePackageName(resID) + '/' +
                    context.resources.getResourceTypeName(resID) + '/' +
                    context.resources.getResourceEntryName(resID)
        )
=======
        //do not change this line
        selectedImage!!.setImageBitmap(createBitmap())
        defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
>>>>>>> Stashed changes
    }

    private fun setPhoto(result: ActivityResult) {
        val data: Intent? = result.data
        val contentUri = data!!.data
        selectedImage!!.setImageURI(contentUri)
        defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
    }

    private fun getFileExt(contentUri: Uri?): String? {
        val c = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(c.getType(contentUri!!))
    }

    private fun setBrightnessValue() {
        if(!this::defaultImageBitMap.isInitialized) return

        brightnessValue = brightnessSlider.value.toDouble()

        val bitmap = defaultImageBitMap
        val filteredBitmap = applyBrightnessFilter(bitmap)
        loadImage(filteredBitmap)
    }

    private fun applyBrightnessFilter(originalBitmap: Bitmap) = BrightnessFilter.apply(originalBitmap)

    private fun loadImage(bmp: Bitmap) {
        selectedImage.setImageBitmap(bmp)
    }


    private fun bindViews() {
        selectedImage = findViewById(R.id.ivPhoto)
        brightnessSlider = findViewById(R.id.slBrightness)
        buttonSave = findViewById(R.id.btnSave)
    }

    fun openGallery(view: View) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(i)
    }

<<<<<<< Updated upstream
    companion object {
        var brightnessValue = 0.0
    }

    fun pureBlack(source: Bitmap): Boolean {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = Color.red(pixels[index])
                G = Color.green(pixels[index])
                B = Color.blue(pixels[index])

                if(R != 0) return false
                if(G != 0) return false
                if(B != 0) return false

            }
        }

        return true
    }

=======
    /*
    @RequiresApi(Build.VERSION_CODES.Q)
    fun savePhoto(view: View) {
        saveImage()
    }

    fun saveImage(): Uri? {
        val bitmap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = contentResolver.insert(contentUri, ContentValues())
        val stream = uri?.let { contentResolver.openOutputStream(it) }
        if (stream != null) {
            saveBitmap(bitmap, stream, 100)
        }
        return uri
    }

    fun saveBitmap(bitmap: Bitmap, stream: OutputStream, quality:Int) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    }

     */


    private fun scanFile(context: Context, filePath:String) {
        val file = File(filePath)
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()),
            null, null)
    }

    companion object {
        var brightnessValue = 0.0
        fun saveAndClose(data:Bitmap, fos: OutputStream) {
            data.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        }
    }

    // do not change this function
>>>>>>> Stashed changes
    fun createBitmap(): Bitmap {
        val width = 100
        val height = 100
        val pixels = IntArray(width * height)
        // get pixel array from source

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = x % 100
                G = y % 100
                B = (x+y) % 100

                pixels[index] = Color.rgb(R,G,B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }
}