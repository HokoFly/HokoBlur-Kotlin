package com.hoko.ktblur.api

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.params.Scheme

interface BlurProcessor {

    var radius: Int
    var mode: Mode
    var scheme: Scheme
    var sampleFactor: Float
    var forceCopy: Boolean
    var needUpscale: Boolean
    var translateX: Int
    var translateY: Int


    fun blur(bitmap: Bitmap): Bitmap

    fun blur(view: View): Bitmap

//    fun asyncBlur(bitmap: Bitmap, )



}