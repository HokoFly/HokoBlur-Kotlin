package com.hoko.ktblur.opengl.texture

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import java.lang.ref.WeakReference

class BitmapTexture(bitmap: Bitmap) : AbstractTexture(bitmap.width, bitmap.height){

    private val bitmapWeakRef = WeakReference<Bitmap>(bitmap)

    init {
        create()
    }

    override fun onTextureCreated() {
        check(width() > 0 && height() > 0)

        val bitmap = bitmapWeakRef.get()
        if (bitmap != null && !(bitmap.isRecycled)) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        }

    }
}