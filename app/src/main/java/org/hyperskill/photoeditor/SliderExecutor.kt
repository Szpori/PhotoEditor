package org.hyperskill.photoeditor

import android.graphics.Bitmap
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SliderExecutor(
    val filterApplier: IFilterApplier,
    val slBrightness: Slider,
    val slContrast: Slider,
    val slSaturation: Slider,
    val slGamma: Slider,
    var defaultImageBitMap:Bitmap
) {

    init {
        slBrightness.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
        slContrast.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
        slSaturation.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
        slGamma.addOnChangeListener { slider, value, fromUser ->
            applyFilterChange()
        }
    }

    fun applyFilterChange() {
        GlobalScope.launch(Dispatchers.Main) {
            filterApplier.setBrightness(defaultImageBitMap, slBrightness.value.toInt(), slContrast.value.toInt(), slSaturation.value.toInt(), slGamma.value.toInt())
        }
    }
}