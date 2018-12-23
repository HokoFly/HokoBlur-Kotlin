package com.hoko.ktblur.task

import android.graphics.Bitmap

class AsyncBlurTask {

    interface Callback {

        fun onSuccess(bitmap: Bitmap)

        fun onFailed(error: Throwable)
    }
}