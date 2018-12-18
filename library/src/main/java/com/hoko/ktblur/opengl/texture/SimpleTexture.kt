package com.hoko.ktblur.opengl.texture

import android.opengl.GLES20
import java.nio.Buffer

class SimpleTexture(width: Int, height: Int) : AbstractTexture(width, height) {


    override fun onTextureCreated() {
        check(width() > 0 && height() > 0)

        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width(),
            height(),
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null as Buffer?
        )

    }
}