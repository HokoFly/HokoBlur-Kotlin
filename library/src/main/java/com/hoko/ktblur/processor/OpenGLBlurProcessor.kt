package com.hoko.ktblur.processor

import android.graphics.Bitmap
import com.hoko.ktblur.opengl.offscreen.EglBuffer

internal class OpenGLBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {

    private val eglBuffer: EglBuffer = EglBuffer()

    override fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap {
        check(!bitmap.isRecycled)
        return eglBuffer.getBlurBitmap(bitmap, radius, mode)
    }

}