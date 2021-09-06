package org.hyperskill.photoeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.slider.Slider
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var selectedImage: ImageView
    public lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var brightnessSlider: Slider
    private lateinit var contrastSlider: Slider
    private lateinit var saturationSlider: Slider
    private lateinit var gammaSlider: Slider
    private lateinit var defaultImageBitMap: Bitmap
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        brightnessSlider.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
        contrastSlider.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
        saturationSlider.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
        gammaSlider.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }

        resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setPhoto(result)
            }
        }
    }

    private fun setPhoto(result: ActivityResult) {
        val data: Intent? = result.data
        val contentUri = data!!.data
        //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri)
        //Log.d("tag", "onActivityResult: Gallery Image Uri:  $imageFileName")
        selectedImage!!.setImageURI(contentUri)
        defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
        defaultImageBitMap = resize(defaultImageBitMap, 1224, 1632)
    }

    private fun getFileExt(contentUri: Uri?): String? {
        val c = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(c.getType(contentUri!!))
    }

    private fun applyFilterChange() {

        if(!this::defaultImageBitMap.isInitialized) return

        contrast = contrastSlider.value.toDouble()
        brightnessValue = brightnessSlider.value.toDouble()
        saturation = saturationSlider.value.toDouble()
        gamma = gammaSlider.value.toDouble()


        coroutineScope.launch {
            // defer the call to the Dispatchers IO coroutine scope
            val bitmap = defaultImageBitMap
            // to apply the filter requires some processing  which should not be done on the main thread
            val filteredDeferred = coroutineScope.async(Dispatchers.Default) {
                // filter needs to be done after we have received the original bitmap
                applyContrastBrightnessFilter(bitmap)
            }

            // await the filter to be applied to the original image
            val filteredBitmap = filteredDeferred.await()
            // load the filtered image once we have it
            loadImage(filteredBitmap)
        }
    }

    private fun applyContrastBrightnessFilter(originalBitmap: Bitmap) = ContrastBrightnessFilter.apply(originalBitmap)

    private fun loadImage(bmp: Bitmap) {
        selectedImage.setImageBitmap(bmp)
    }


    private fun bindViews() {
        selectedImage = findViewById(R.id.ivPhoto)
        brightnessSlider = findViewById(R.id.slBrightness)
        contrastSlider = findViewById(R.id.slContrast)
        saturationSlider = findViewById(R.id.slSaturation)
        gammaSlider = findViewById(R.id.slGamma)
    }

    fun openGallery(view: View) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(i)
    }

    fun savePhoto(view: View) {
        val bitmap = selectedImage.getDrawable().toBitmap()

        var outStream: FileOutputStream? = null
        val path = this.getExternalFilesDir(null)?.path?.split("/Android")?.get(0);
        val filePath = path + "/Pictures"
        Log.d("tag", "onActivityResult: filePath:  $filePath")
        val dir = File(filePath)
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.flush()
        outStream.close()

        scanFile(this, filePath)
    }

    private fun scanFile(context: Context, filePath:String) {
        val file = File(filePath)
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()),
            null, null)
    }

    private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var image = image
        val width = image.width
        val height = image.height
        return if (maxHeight < height || maxWidth < width) {
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            image
        } else {
            image
        }
    }

    companion object {
        var brightnessValue = 0.0
        var contrast = 0.0
        var saturation = 0.0
        var gamma = 1.0
    }
}