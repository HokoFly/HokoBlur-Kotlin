package com.hoko.ktblur.task

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurCallback
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal abstract class AsyncBlurTask<in T>(
    private val block: BlurCallback.() -> Unit,
    private val target: T,
    private val dispatcher: BlurResultDispatcher
) {

    fun post(): Job {
        return CoroutineScope(BlurTaskManager.BLUR_DISPATCHER).launch {
            val callback = BlurCallback().apply {
                this.block()
            }
            val success = false
            var exception: Throwable? = null
            val bitmap: Bitmap? = null
            kotlin.runCatching {
                applyBlur(target)
            }.getOrElse {
                exception = it
            }
            dispatcher.dispatch {
                if (success) {
                    callback.onSuccess?.invoke(bitmap)
                } else {
                    callback.onFailed?.invoke(exception)
                }
            }
        }

    }

    abstract suspend fun applyBlur(target: T) : Bitmap

}

internal class BitmapAsyncBlurTask(
    private val blurProcessor: BlurProcessor,
    block: BlurCallback.() -> Unit,
    bitmap: Bitmap,
    dispatcher: BlurResultDispatcher
) : AsyncBlurTask<Bitmap>(block, bitmap, dispatcher) {
    override suspend fun applyBlur(target: Bitmap): Bitmap {
        return blurProcessor.blur(target)
    }
}

internal class ViewAsyncBlurTask (private val blurProcessor: BlurProcessor,
                                  block: BlurCallback.() -> Unit,
                                  view: View,
                                  dispatcher: BlurResultDispatcher
) : AsyncBlurTask<View>(block, view, dispatcher) {

    override suspend fun applyBlur(target: View): Bitmap {
        return blurProcessor.blur(target)
    }
}