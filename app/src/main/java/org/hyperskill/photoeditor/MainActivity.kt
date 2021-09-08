package org.hyperskill.photoeditor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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


class MainActivity : AppCompatActivity() {

    private lateinit var selectedImage: ImageView
    public lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var brightnessSlider: Slider
    private lateinit var defaultImageBitMap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        brightnessSlider.addOnChangeListener { slider, value, fromUser ->
            setBrightnessValue()
        }

        resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setPhoto(result)
            }
        }

        //selectedImage!!.setImageResource(R.drawable.download)
        //defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
    }

    private fun setPhoto(result: ActivityResult) {
        val data: Intent? = result.data
        val contentUri = data!!.data
        //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri)
        //Log.d("tag", "onActivityResult: Gallery Image Uri:  $imageFileName")
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
    }

    fun openGallery(view: View) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(i)
    }

    companion object {
        var brightnessValue = 0.0
    }
}