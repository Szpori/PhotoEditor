package org.hyperskill.photoeditor

import android.graphics.Bitmap
import android.widget.ImageView
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface IFilterApplier {
    val selectedImage:ImageView
    val parentContext: CoroutineContext

    suspend fun applyFilterChange(defaultImageBitMap: Bitmap, brightnessValue:Int, contrast:Int, saturation:Int, gamma:Int) {
    }
}

