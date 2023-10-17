package com.hoko.ktblur.task

import android.graphics.Bitmap


@DslMarker
internal annotation class BlurCallbackDSL

@BlurCallbackDSL
class BlurCallback {
    internal var onSuccess: ((Bitmap?) -> Unit)? = null
    internal var onFailed: ((Throwable?) -> Unit)? = null

    fun onSuccess(onSuccess: ((Bitmap?) -> Unit)?) {
        this.onSuccess = onSuccess
    }

    fun onFailed(onFailed: ((Throwable?) -> Unit)?) {
        this.onFailed = onFailed
    }
}