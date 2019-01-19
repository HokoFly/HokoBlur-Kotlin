package com.hoko.ktblur.task

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher
import com.hoko.ktblur.task.AndroidBlurResultDispatcher.Companion.MAIN_THREAD_DISPATCHER

sealed class AsyncBlurTask<in T>(
    protected val blurProcessor: BlurProcessor,
    private val callback: Callback,
    private val target: T
) : Runnable {

    private var blurResultDispatcher: BlurResultDispatcher = MAIN_THREAD_DISPATCHER

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
    bitmap: Bitmap
) : AsyncBlurTask<Bitmap>(blurProcessor, callback, bitmap) {
    override fun makeBlur(target: Bitmap): Bitmap {
        return blurProcessor.blur(target)
    }


}

class ViewAsyncBlurTask (blurProcessor: BlurProcessor,
                         callback: AsyncBlurTask.Callback,
                         view: View
) : AsyncBlurTask<View>(blurProcessor, callback, view) {

    override fun makeBlur(target: View): Bitmap {
        return blurProcessor.blur(target)
    }
}