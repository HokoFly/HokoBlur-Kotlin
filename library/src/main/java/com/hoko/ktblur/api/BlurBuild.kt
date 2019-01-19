package com.hoko.ktblur.api

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.params.Scheme
import com.hoko.ktblur.task.AsyncBlurTask
import java.util.concurrent.Future

interface BlurBuild {

    fun context(context: Context): BlurBuild

    fun mode(mode: Mode): BlurBuild

    fun scheme(scheme: Scheme): BlurBuild

    fun radius(radius: Int): BlurBuild

    fun sampleFactor(sampleFactor: Float): BlurBuild

    fun forceCopy(forceCopy: Boolean): BlurBuild

    fun needUpscale(needUpscale: Boolean): BlurBuild

    fun translateX(translateX: Int): BlurBuild

    fun translateY(translateY: Int): BlurBuild

    fun dispatcher(dispatcher: BlurResultDispatcher): BlurBuild

    fun processor(): BlurProcessor

    fun blur(bitmap: Bitmap): Bitmap

    fun blur(view: View): Bitmap

    fun asyncBlur(bitmap: Bitmap, callback: AsyncBlurTask.Callback) : Future<*>

    fun asyncBlur(view: View, callback: AsyncBlurTask.Callback) : Future<*>

}