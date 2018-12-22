package com.hoko.ktblur.opengl.texture

import android.opengl.GLES20
import com.hoko.ktblur.api.Texture

abstract class AbstractTexture(val width: Int, val height: Int) : Texture {
    private var textureId: Int = 0

    override fun create() {
        val textureIds = IntArray(1)

        GLES20.glGenTextures(1, textureIds, 0)

        textureId = textureIds[0]

        if (textureId != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST.toFloat())
            onTextureCreated()
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    abstract fun onTextureCreated()

    override fun delete() {
        if (textureId != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
        }
    }

    override fun id(): Int {
        return textureId
    }

    override fun width(): Int {
        return width
    }

    override fun height(): Int {
        return height
    }
}