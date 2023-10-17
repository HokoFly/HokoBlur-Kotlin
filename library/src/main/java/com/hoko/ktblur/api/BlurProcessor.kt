package com.hoko.ktblur.api

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.task.BlurCallback
import kotlinx.coroutines.Job

interface BlurProcessor {
    var radius: Int
    var mode: Mode
    var scheme: Scheme
    var sampleFactor: Float
    var forceCopy: Boolean
    var needUpscale: Boolean
    var translateX: Int
    var translateY: Int
    var dispatcher: BlurResultDispatcher

    fun blur(bitmap: Bitmap): Bitmap

    fun blur(view: View): Bitmap

    fun asyncBlur(bitmap: Bitmap, block: BlurCallback.() -> Unit): Job

    fun asyncBlur(view: View, block: BlurCallback.() -> Unit): Job

}