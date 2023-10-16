package com.hoko.ktblur.opengl.cache

import com.hoko.ktblur.api.FrameBuffer
import com.hoko.ktblur.opengl.framebuffer.FrameBufferFactory

internal object FrameBufferCache {
    private val cachePool = object : CachePool<Unit, FrameBuffer>(32) {
        override fun create(key: Unit): FrameBuffer {
            return FrameBufferFactory.create()
        }

        override fun checkHit(key: Unit, value: FrameBuffer): Boolean {
            return true
        }

        override fun entryDeleted(removed: FrameBuffer) {
            removed.delete()
        }
    }

    fun getFrameBuffer(): FrameBuffer {
        return cachePool.get(Unit)
    }


    fun recycleFrameBuffer(frameBuffer: FrameBuffer) {
        cachePool.put(frameBuffer)
    }

    fun clear() {
        cachePool.evictAll()
    }

}