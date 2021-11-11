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


class MainActivity : AppCompatActivity() {

    private lateinit var selectedImage: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
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

        //do not change this line
        selectedImage!!.setImageBitmap(createBitmap())

        defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
    }

    private fun setPhoto(result: ActivityResult) {
        val data: Intent? = result.data
        val contentUri = data!!.data
        selectedImage!!.setImageURI(contentUri)
        defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
    }

    private fun setBrightnessValue() {
        if(!this::defaultImageBitMap.isInitialized) return

        brightnessValue = brightnessSlider.value.toDouble()

        val bitmap = defaultImageBitMap
        val filteredBitmap = applyBrightnessFilter(bitmap)
        loadImage(filteredBitmap)
    }

    private fun applyBrightnessFilter(originalBitmap: Bitmap) = BrightnessFilter.apply(
        brightnessValue, originalBitmap)

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

    // do not change this function
    fun createBitmap(): Bitmap {
        val width = 200
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