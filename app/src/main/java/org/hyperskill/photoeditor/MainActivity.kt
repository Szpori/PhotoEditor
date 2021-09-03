package org.hyperskill.photoeditor

import android.app.Activity
import android.content.Intent
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


class MainActivity : AppCompatActivity() {

    private lateinit var selectedImage: ImageView
    public lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

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
    }

    private fun getFileExt(contentUri: Uri?): String? {
        val c = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(c.getType(contentUri!!))
    }


    private fun bindViews() {
        selectedImage = findViewById(R.id.ivPhoto)
    }

    fun openGallery(view: View) {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(i)
    }
}