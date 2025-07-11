package com.hoko.ktblur.demo.vm

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoko.ktblur.demo.model.BlurOp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by yuxfzju on 2023/10/26
 */
class MultiBlurViewModel : ViewModel() {

    val bitmapLiveData: MutableLiveData<Bitmap> = MutableLiveData()
    var blurRadius = 5
    var resIndex = 0

    val blurFlow = MutableStateFlow(BlurOp(blurRadius))


    fun setImage(@DrawableRes id: Int, resources: Resources) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeResource(resources, id)
            }
            bitmapLiveData.value = bitmap
        }
    }

    fun changeBlurRadius(radius: Int) {
        blurRadius = radius
        blurFlow.value = BlurOp(radius)
    }


}