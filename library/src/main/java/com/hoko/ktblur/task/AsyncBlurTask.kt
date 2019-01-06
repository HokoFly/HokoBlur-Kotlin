package com.hoko.ktblur.task

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher
import com.hoko.ktblur.task.AndroidBlurResultDispatcher.Companion.MAIN_THREAD_DISPATCHER

sealed class AsyncBlurTask(
    protected val blurProcessor: BlurProcessor,
    private val callback: Callback
) : Runnable {

    var blurResultDispatcher: BlurResultDispatcher = MAIN_THREAD_DISPATCHER

    abstract fun makeBlur() : Bitmap

    override fun run() {

        val blurResult = BlurResult(callback)

        try {
            blurResult.let {
                it.bitmap = makeBlur()
                it.success = true
            }
        } catch (e: Throwable) {
            blurResult.let {
                it.error = e
                it.success = false
            }
        } finally {
            blurResultDispatcher.dispatch(blurResult)
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
    private val bitmap: Bitmap
) : AsyncBlurTask(blurProcessor, callback) {
    override fun makeBlur(): Bitmap {
        return blurProcessor.blur(bitmap)
    }


}

class ViewAsyncBlurTask (blurProcessor: BlurProcessor,
                         callback: AsyncBlurTask.Callback, private val view: View
) : AsyncBlurTask(blurProcessor, callback) {

    override fun makeBlur(): Bitmap {
        return blurProcessor.blur(view)
    }
}