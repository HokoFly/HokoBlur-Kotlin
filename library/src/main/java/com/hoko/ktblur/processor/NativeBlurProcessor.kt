package com.hoko.ktblur.processor

import android.graphics.Bitmap
import com.hoko.ktblur.filter.NativeBlurFilter

class NativeBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {
    override fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap {

        NativeBlurFilter.doFullBlur(mode, bitmap, radius)

        return bitmap

    }
}