package com.hoko.ktblur.task

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurCallback
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.BlurResultDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal abstract class AsyncBlurTask<in T>(
    private val block: BlurCallback.() -> Unit,
    private val target: T,
    private val resultDispatcher: BlurResultDispatcher,
    private val taskDispatcher: CoroutineContext
) {

    fun post(): Job {
        return CoroutineScope(taskDispatcher).launch {
            val callback = BlurCallback().apply {
                this.block()
            }
            var success = false
            var exception: Throwable? = null
            var bitmap: Bitmap? = null
            kotlin.runCatching {
                bitmap = applyBlur(target)
                success = true
            }.onFailure {
                exception = it
            }
            resultDispatcher.dispatch {
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
    resultDispatcher: BlurResultDispatcher,
    taskDispatcher: CoroutineContext = BlurTaskManager.BLUR_DISPATCHER
) : AsyncBlurTask<Bitmap>(block, bitmap, resultDispatcher, taskDispatcher) {
    override suspend fun applyBlur(target: Bitmap): Bitmap {
        return blurProcessor.blur(target)
    }
}

internal class ViewAsyncBlurTask (private val blurProcessor: BlurProcessor,
                                  block: BlurCallback.() -> Unit,
                                  view: View,
                                  resultDispatcher: BlurResultDispatcher,
                                  taskDispatcher: CoroutineContext = BlurTaskManager.BLUR_DISPATCHER,
) : AsyncBlurTask<View>(block, view, resultDispatcher, taskDispatcher) {

    override suspend fun applyBlur(target: View): Bitmap {
        return blurProcessor.blur(target)
    }
}