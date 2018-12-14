package com.hoko.ktblur.processor

import android.graphics.Bitmap
import android.view.View
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.ext.getBitmap
import com.hoko.ktblur.ext.scale
import com.hoko.ktblur.ext.translate
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.params.Scheme

abstract class AbstractBlurProcessor(builder: HokoBlurBuild) : BlurProcessor {

    override var radius: Int = builder.radius
    override var mode: Mode = builder.mode
    override var scheme: Scheme = builder.scheme
    override var sampleFactor: Float = builder.sampleFactor
    override var forceCopy: Boolean = builder.forceCopy
    override var needUpscale: Boolean = builder.needUpscale
    override var translateX: Int = builder.translateX
    override var translateY: Int = builder.translateY

    override fun blur(bitmap: Bitmap): Bitmap {
        return blur(bitmap, true)
    }

    override fun blur(view: View): Bitmap {
        return view.getBitmap(translateX, translateY, sampleFactor)
    }

    private fun blur(bitmap: Bitmap, parallel: Boolean): Bitmap {
        checkParams()

        val inBitmap = if (forceCopy) {
            bitmap.copy(bitmap.config, true)
        } else {
            bitmap
        }

        val scaledBitmap = inBitmap.translate(translateX, translateY).scale(sampleFactor)

        return realBlur(scaledBitmap, parallel).scale(if (needUpscale) (1.0f / sampleFactor) else 1.0f)

    }

    protected abstract fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap

    private fun checkParams() {
        radius = if (radius <= 0) 1 else radius
        sampleFactor = if (sampleFactor < 1.0f) 1.0f else sampleFactor
    }




}