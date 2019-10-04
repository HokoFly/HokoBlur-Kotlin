package com.hoko.ktblur.task

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher

sealed class AsyncBlurTask<in T>(
    protected val blurProcessor: BlurProcessor,
    private val block: Callback.() -> Unit,
    private val target: T,
    private val dispatcher: BlurResultDispatcher
) : Runnable {

    abstract fun makeBlur(target: T) : Bitmap

    override fun run() {
        val callback = BlurCallback().apply {
            this.block()
        }
        val blurResult = BlurResult(callback)

        try {
            blurResult.apply {
                bitmap = makeBlur(target)
                success = true
            }
        } catch (e: Throwable) {
            blurResult.apply {
                error = e
                success = false
            }
        } finally {
            dispatcher.dispatch(BlurResultRunnable.of(blurResult))
        }

    }

    interface Callback {
        var onSuccess: ((Bitmap?) -> Unit)?
        var onFailed: ((Throwable?) -> Unit)?
        fun onSuccess(onSuccess: ((Bitmap?) -> Unit)?)
        fun onFailed(onFailed: ((Throwable?) -> Unit)?)
    }
}

class BitmapAsyncBlurTask(
    blurProcessor: BlurProcessor,
    block: Callback.() -> Unit,
    bitmap: Bitmap,
    dispatcher: BlurResultDispatcher
) : AsyncBlurTask<Bitmap>(blurProcessor, block, bitmap, dispatcher) {
    override fun makeBlur(target: Bitmap): Bitmap {
        return blurProcessor.blur(target)
    }
}

class ViewAsyncBlurTask (blurProcessor: BlurProcessor,
                         block: Callback.() -> Unit,
                         view: View,
                         dispatcher: BlurResultDispatcher
) : AsyncBlurTask<View>(blurProcessor, block, view, dispatcher) {

    override fun makeBlur(target: View): Bitmap {
        return blurProcessor.blur(target)
    }
}