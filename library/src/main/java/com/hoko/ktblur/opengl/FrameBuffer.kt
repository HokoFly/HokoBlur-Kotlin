package com.hoko.ktblur.opengl

import android.opengl.GLES20

internal class FrameBuffer(private var frameBufferId: Int = 0) {
    companion object {
        fun create(id: Int = 0): FrameBuffer {
            return FrameBuffer(id)
        }
    }

    private lateinit var texture: Texture

    init {
        val frameBufferIds = IntArray(1)
        GLES20.glGenFramebuffers(1, frameBufferIds, 0)
        frameBufferId = frameBufferIds[0]
    }

    fun bindTexture(texture: Texture) {
        this.texture = texture.also {
            check(it.id != 0)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)
            GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, it.id, 0
            )
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
    }

    fun bindSelf() {
        if (frameBufferId != 0) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId)
        }
    }

    fun delete() {
        if (frameBufferId != 0) {
            GLES20.glDeleteFramebuffers(1, intArrayOf(frameBufferId), 0)
        }
    }
}