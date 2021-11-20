package org.hyperskill.photoeditor

import android.graphics.Bitmap
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SliderExecutor(
    val brightnessFilter: IBrightnessFilter,
    slBrightness:Slider,
    var defaultImageBitMap:Bitmap
) {
    val slBrightness:Slider = slBrightness

    init {
        slBrightness.addOnChangeListener { slider, value, fromUser ->
            setBrightnessValue()
        }
    }

    fun setBrightnessValue() {
        GlobalScope.launch(Dispatchers.Main) {
            brightnessFilter.setBrightness(defaultImageBitMap, slBrightness.value.toInt())
        }
    }
}