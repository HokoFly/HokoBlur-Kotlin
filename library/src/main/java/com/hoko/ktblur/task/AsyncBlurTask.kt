package com.hoko.ktblur.task

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher

sealed class AsyncBlurTask<in T>(
    protected val blurProcessor: BlurProcessor,
    private val callback: Callback,
    private val target: T,
    private val dispatcher: BlurResultDispatcher
) : Runnable {

    abstract fun makeBlur(target: T) : Bitmap

    override fun run() {

        val blurResult = BlurResult(callback)

        try {
            blurResult.let {
                it.bitmap = makeBlur(target)
                it.success = true
            }
        } catch (e: Throwable) {
            blurResult.let {
                it.error = e
                it.success = false
            }
        } finally {
            dispatcher.dispatch(BlurResultRunnable.of(blurResult))
        }

    }

    interface Callback {

        fun onSuccess(bitmap: Bitmap?)

        fun onFailed(error: Throwable?)
    }
}

class BitmapAsyncBlurTask(
    blurProcessor: BlurProcessor,
    callback: Callback,
    bitmap: Bitmap,
    dispatcher: BlurResultDispatcher
) : AsyncBlurTask<Bitmap>(blurProcessor, callback, bitmap, dispatcher) {
    override fun makeBlur(target: Bitmap): Bitmap {
        return blurProcessor.blur(target)
    }
}

class ViewAsyncBlurTask (blurProcessor: BlurProcessor,
                         callback: AsyncBlurTask.Callback,
                         view: View,
                         dispatcher: BlurResultDispatcher
) : AsyncBlurTask<View>(blurProcessor, callback, view, dispatcher) {

    override fun makeBlur(target: View): Bitmap {
        return blurProcessor.blur(target)
    }
}