package com.hoko.ktblur.processor

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher
import com.hoko.ktblur.ext.getBitmap
import com.hoko.ktblur.ext.scale
import com.hoko.ktblur.ext.translate
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.api.Scheme
import com.hoko.ktblur.task.BitmapAsyncBlurTask
import com.hoko.ktblur.api.BlurCallback
import com.hoko.ktblur.task.ViewAsyncBlurTask
import kotlinx.coroutines.Job
import java.lang.Float.max

internal abstract class AbstractBlurProcessor(builder: HokoBlurBuild) : BlurProcessor {

    override var radius: Int = builder.radius
    override var mode: Mode = builder.mode
    override val scheme: Scheme = builder.scheme
    override var sampleFactor: Float = builder.sampleFactor
    override var forceCopy: Boolean = builder.forceCopy
    override var translateX: Int = builder.translateX
    override var translateY: Int = builder.translateY
    override var dispatcher: BlurResultDispatcher = builder.dispatcher

    override fun blur(bitmap: Bitmap): Bitmap {
        return blur(bitmap, true)
    }

    override fun blur(view: View): Bitmap {
        if (radius <= 0) {
            return view.getBitmap(translateX, translateY, 1.0f)
        }
        return realBlur(view.getBitmap(translateX, translateY, sampleFactor), true)
            .scale(1.0f / sampleFactor)
    }

    private fun blur(bitmap: Bitmap, parallel: Boolean): Bitmap {
        checkParams()
        val inBitmap = if (forceCopy) {
            bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }
        if (radius <= 0) {
            return inBitmap
        }
        val scaledBitmap = inBitmap.translate(translateX, translateY).scale(sampleFactor)
        return realBlur(scaledBitmap, parallel).scale(1.0f / sampleFactor)
    }

    protected abstract fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap

    override fun asyncBlur(bitmap: Bitmap, block: BlurCallback.() -> Unit): Job {
        return BitmapAsyncBlurTask(this, block, bitmap, dispatcher).post()
    }

    override fun asyncBlur(view: View, block: BlurCallback.() -> Unit): Job {
        return ViewAsyncBlurTask(this, block, view, dispatcher).post()
    }

    private fun checkParams() {
        sampleFactor = max(sampleFactor, 1.0f)
    }

    override fun close() {

    }


}