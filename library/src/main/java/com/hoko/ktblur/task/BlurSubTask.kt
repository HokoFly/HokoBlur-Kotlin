package com.hoko.ktblur.task

import android.graphics.Bitmap
import com.hoko.ktblur.filter.NativeBlurFilter
import com.hoko.ktblur.filter.OriginBlurFilter
import com.hoko.ktblur.api.Direction
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.api.Scheme

internal class BlurSubTask(
    private val scheme: Scheme,
    private val mode: Mode,
    private val bitmapOut: Bitmap,
    private val radius: Int,
    private val index: Int,
    private val cores: Int,
    private val direction: Direction
) : Runnable {

    override fun run() {
        check(!bitmapOut.isRecycled)
        require(cores > 0)
        when (scheme) {
            Scheme.NATIVE -> NativeBlurFilter.doBlur(mode, bitmapOut, radius, cores, index, direction)
            Scheme.KOTLIN -> OriginBlurFilter.doBlur(mode, bitmapOut, radius, cores, index, direction)
            Scheme.OPENGL -> throw UnsupportedOperationException("Blur in parallel not supported !")
            Scheme.RENDERSCRIPT -> {
                //RenderScript support built-in parallel computation
            }
        }
    }
}
