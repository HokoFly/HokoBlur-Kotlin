package com.hoko.ktblur.processor

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurBuild
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.api.Scheme
import com.hoko.ktblur.task.AndroidBlurResultDispatcher
import com.hoko.ktblur.api.BlurCallback
import kotlinx.coroutines.Job

internal class HokoBlurBuild(var context: Context) : BlurBuild {
    internal var radius: Int = 10
    internal var mode: Mode = Mode.STACK
    internal var scheme: Scheme = Scheme.NATIVE
    internal var sampleFactor: Float = 5.0f
    internal var forceCopy: Boolean = false
    internal var needUpscale: Boolean = true
    internal var translateX: Int = 0
    internal var translateY: Int = 0
    internal var dispatcher: BlurResultDispatcher = AndroidBlurResultDispatcher.MAIN_THREAD_DISPATCHER

    override fun context(context: Context): BlurBuild = apply {
        this.context = context
    }

    override fun mode(mode: Mode): BlurBuild = apply {
        this.mode = mode
    }

    override fun scheme(scheme: Scheme): BlurBuild = apply {
        this.scheme = scheme
    }

    override fun radius(radius: Int): BlurBuild = apply {
        this.radius = radius
    }

    override fun sampleFactor(sampleFactor: Float): BlurBuild = apply {
        this.sampleFactor = sampleFactor
    }

    override fun forceCopy(forceCopy: Boolean): BlurBuild = apply {
        this.forceCopy = forceCopy
    }

    override fun needUpscale(needUpscale: Boolean): BlurBuild = apply {
        this.needUpscale = needUpscale
    }

    override fun translateX(translateX: Int): BlurBuild = apply {
        this.translateX = translateX
    }

    override fun translateY(translateY: Int): BlurBuild = apply {
        this.translateY = translateY
        return this
    }

    override fun dispatcher(dispatcher: BlurResultDispatcher): BlurBuild = apply {
        this.dispatcher = dispatcher
    }

    override fun processor(): BlurProcessor {
        return BlurProcessorFactory.getBlurProcessor(scheme, this)
    }

    override fun blur(bitmap: Bitmap): Bitmap {
        return processor().blur(bitmap)
    }

    override fun blur(view: View): Bitmap {
        return processor().blur(view)
    }

    override fun asyncBlur(bitmap: Bitmap, block: BlurCallback.() -> Unit): Job {
        return processor().asyncBlur(bitmap, block)
    }

    override fun asyncBlur(view: View, block: BlurCallback.() -> Unit): Job {
        return processor().asyncBlur(view, block)
    }
}