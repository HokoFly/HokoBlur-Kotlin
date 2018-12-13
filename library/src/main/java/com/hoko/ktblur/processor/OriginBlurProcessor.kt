package com.hoko.ktblur.processor

import android.graphics.Bitmap
import com.hoko.ktblur.filter.OriginBlurFilter

class OriginBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {

    override fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap {

        OriginBlurFilter.doFullBlur(mode, bitmap, radius)

        return bitmap
    }
}