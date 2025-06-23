package com.hoko.ktblur.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

internal fun Bitmap.scale(factor: Float): Bitmap {
    if (factor == 1.0f) {
        return this
    }
    val scale = 1.0f / factor
    val newWidth: Int = (width * scale).toInt()
    val newHeight: Int = (height * scale).toInt()
    val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, config ?: Bitmap.Config.ARGB_8888)
    val canvas = Canvas(scaledBitmap)
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(scale, scale)
    canvas.drawBitmap(this, scaleMatrix, Paint())
    return scaledBitmap
}

internal fun Bitmap.translate(translateX: Int, translateY: Int): Bitmap {
    if (translateX == 0 && translateY == 0) {
        return this
    }
    return Bitmap.createBitmap(this, translateX, translateY, this.width - translateX, this.height - translateY)
}

internal fun Bitmap.replaceWithPixels(pixels: IntArray, x: Int, y: Int, deltaX: Int, deltaY: Int) {
    this.nativeReplaceWithPixels(pixels, x, y, deltaX, deltaY)
}

private external fun Bitmap.nativeReplaceWithPixels(pixels: IntArray, x: Int, y: Int, deltaX: Int, deltaY: Int)