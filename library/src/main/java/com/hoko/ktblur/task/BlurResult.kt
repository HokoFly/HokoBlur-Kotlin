package com.hoko.ktblur.task

import android.graphics.Bitmap

data class BlurResult(val callback: AsyncBlurTask.Callback) {
    var isSucccess: Boolean = false

    var bitmap: Bitmap? = null

    var error: Throwable? = null
}