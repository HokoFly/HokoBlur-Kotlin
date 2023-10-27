package com.hoko.ktblur.api

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import kotlinx.coroutines.Job

interface BlurBuild {

    fun context(context: Context): BlurBuild

    fun mode(mode: Mode): BlurBuild

    fun scheme(scheme: Scheme): BlurBuild

    fun radius(radius: Int): BlurBuild

    fun sampleFactor(sampleFactor: Float): BlurBuild

    fun forceCopy(forceCopy: Boolean): BlurBuild

    fun translateX(translateX: Int): BlurBuild

    fun translateY(translateY: Int): BlurBuild

    fun dispatcher(dispatcher: BlurResultDispatcher): BlurBuild

    fun processor(): BlurProcessor

    fun blur(bitmap: Bitmap): Bitmap

    fun blur(view: View): Bitmap

    fun asyncBlur(bitmap: Bitmap, block: BlurCallback.() -> Unit): Job

    fun asyncBlur(view: View, block: BlurCallback.() -> Unit): Job

}