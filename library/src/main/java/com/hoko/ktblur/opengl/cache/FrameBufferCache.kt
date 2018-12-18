package com.hoko.ktblur.opengl.cache

import com.hoko.ktblur.api.FrameBuffer
import com.hoko.ktblur.opengl.framebuffer.FrameBufferFactory

object FrameBufferCache {
    @Volatile private var sDisplayFrameBuffer: FrameBuffer? = null

    private val cachePool = object : CachePool<Any, FrameBuffer>() {
        override fun create(key: Any): FrameBuffer {
            return FrameBufferFactory.create()
        }

        override fun checkHit(key: Any, value: FrameBuffer): Boolean {
            return true
        }

        override fun entryDeleted(removed: FrameBuffer) {
            removed.delete()
        }
    }

    fun getFrameBuffer(): FrameBuffer {
        return cachePool.get(Any())
    }


    fun getDisplayFrameBuffer(): FrameBuffer? {
        if (sDisplayFrameBuffer == null) {
            synchronized(this) {
                if (sDisplayFrameBuffer == null) {
                    sDisplayFrameBuffer = FrameBufferFactory.getDisplayFrameBuffer()
                }
            }
        }

        return sDisplayFrameBuffer
    }
    fun recycleFrameBuffer(frameBuffer: FrameBuffer) {
        cachePool.put(frameBuffer)
    }

    fun clear() {
        cachePool.evictAll()
        synchronized(this) {
            sDisplayFrameBuffer?.delete()
            sDisplayFrameBuffer = null
        }

    }

}