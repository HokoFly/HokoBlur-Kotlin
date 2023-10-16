package com.hoko.ktblur.opengl.framebuffer

import com.hoko.ktblur.api.FrameBuffer

internal class FrameBufferFactory {
    companion object {
        fun create(): FrameBuffer {
            return SimpleFrameBuffer()
        }

        fun create(id: Int): FrameBuffer {
            return SimpleFrameBuffer(id)
        }

    }
}