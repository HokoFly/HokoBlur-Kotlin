package com.hoko.ktblur.filter

import android.graphics.Bitmap
import com.hoko.ktblur.ext.replaceWithPixels
import com.hoko.ktblur.params.Direction
import com.hoko.ktblur.params.Mode

object OriginBlurFilter {
    fun doFullBlur(mode: Mode, bitmap: Bitmap, radius: Int) {
        val w = bitmap.width
        val h = bitmap.height

        val pixels = IntArray(w * h) { 0 }
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        when (mode) {
            Mode.BOX -> BoxBlurFilter.doBlur(pixels, w, h, radius, Direction.BOTH)
            Mode.STACK -> StackBlurFilter.doBlur(pixels, w, h, radius, Direction.BOTH)
            Mode.GAUSSIAN -> GaussianBlurFilter.doBlur(pixels, w, h, radius, Direction.BOTH)
        }

        if (bitmap.isMutable) {
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        } else {
            bitmap.replaceWithPixels(pixels, 0, 0, w, h)
        }
    }
}