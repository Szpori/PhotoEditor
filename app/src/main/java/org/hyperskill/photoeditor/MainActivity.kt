package org.hyperskill.photoeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import kotlinx.coroutines.*
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var selectedImage: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var brightnessSlider: Slider
    private lateinit var contrastSlider: Slider
    private lateinit var saturationSlider: Slider
    private lateinit var gammaSlider: Slider
    private lateinit var buttonSave: Button
    private lateinit var filterApplier:FilterApplier

    private lateinit var defaultImageBitMap:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        buttonSave.setOnClickListener {
            saveImage()
        }

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

        //do not change this line
        selectedImage!!.setImageBitmap(createBitmap())
        defaultImageBitMap = (selectedImage.getDrawable() as BitmapDrawable).bitmap

        filterApplier = FilterApplier(selectedImage, Dispatchers.Default)
    }

    private fun applyFilterChange() {
        GlobalScope.launch(Dispatchers.Main) {
            filterApplier.applyFilterChange(defaultImageBitMap, brightnessSlider.value.toInt(), contrastSlider.value.toInt(), saturationSlider.value.toInt(), gammaSlider.value.toInt())
        }
    }

    fun saveImage() {
        val bitmap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        //val uri = Uri.parse("Bad uri")
        val uri = contentResolver.insert(contentUri, ContentValues())
        val stream = uri?.let { contentResolver.openOutputStream(it) }
        saveBitmap(bitmap, stream!!, 100)
    }

    fun saveBitmap(bitmap: Bitmap, stream: OutputStream, quality:Int) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    }

    private fun setPhoto(result: ActivityResult) {
        val data: Intent? = result.data
        val contentUri = data!!.data
        selectedImage!!.setImageURI(contentUri)
        val bitmap = (selectedImage.getDrawable() as BitmapDrawable).bitmap
        defaultImageBitMap = resize(bitmap, 1224, 1632)
    }

    private fun bindViews() {
        selectedImage = findViewById(R.id.ivPhoto)
        brightnessSlider = findViewById(R.id.slBrightness)
        contrastSlider = findViewById(R.id.slContrast)
        saturationSlider = findViewById(R.id.slSaturation)
        gammaSlider = findViewById(R.id.slGamma)
        buttonSave = findViewById(R.id.btnSave)
    }

    fun openGallery(view: View) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(i)
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
                R = x % 100 + 40
                G = y % 100 + 80
                B = (x+y) % 100 + 120

                pixels[index] = Color.rgb(R,G,B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }
}