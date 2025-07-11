package com.hoko.ktblur.processor

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.hoko.ktblur.api.BlurCallback
import com.hoko.ktblur.opengl.offscreen.EglBuffer
import com.hoko.ktblur.task.BitmapAsyncBlurTask
import com.hoko.ktblur.task.BlurTaskManager
import com.hoko.ktblur.task.ViewAsyncBlurTask
import kotlinx.coroutines.Job

internal class OpenGLBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {

    private val eglBuffer: EglBuffer = EglBuffer()

    override fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap {
        check(!bitmap.isRecycled)
        runCatching {
            return eglBuffer.getBlurBitmap(bitmap, radius, mode)
        }.onFailure {
            Log.w("OpenGLBlurProcessor", "Blur the bitmap error", it)
        }
        return bitmap
    }


    override fun asyncBlur(bitmap: Bitmap, block: BlurCallback.() -> Unit): Job {
        return BitmapAsyncBlurTask(this, block, bitmap, dispatcher, BlurTaskManager.TASK_QUEUE_DISPATCHER).post()
    }

    override fun asyncBlur(view: View, block: BlurCallback.() -> Unit): Job {
        return ViewAsyncBlurTask(this, block, view, dispatcher, BlurTaskManager.TASK_QUEUE_DISPATCHER).post()
    }

    override fun close() {
        eglBuffer.close()
    }
}