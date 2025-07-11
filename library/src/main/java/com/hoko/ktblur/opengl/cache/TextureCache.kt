package com.hoko.ktblur.opengl.cache

import com.hoko.ktblur.opengl.Texture
import java.util.ArrayDeque
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by yuxfzju on 2025/7/10
 */
internal object TextureCache {
    private const val TAG = "TextureCache"
    private const val MAX_POOL_SIZE = 20

    private val sizePools: MutableMap<String, TexturePool> by lazy {
        ConcurrentHashMap<String, TexturePool>()
    }

    // 获取纹理
    fun acquireTexture(width: Int, height: Int): Texture {
        val sizeKey = createSizeKey(width, height)
        var pool = sizePools[sizeKey] ?: run {
            synchronized(sizePools) {
                sizePools[sizeKey] ?: run {
                    TexturePool(width, height).apply {
                        sizePools.put(sizeKey, this)
                    }
                }
            }
        }
        return pool.acquire()
    }

    fun releaseTexture(texture: Texture?) {
        if (texture == null) {
            return
        }
        val sizeKey = createSizeKey(texture.width, texture.height)
        val pool = sizePools[sizeKey]
        if (pool != null) {
            pool.release(texture)
        } else {
            texture.delete()
        }
    }

    private fun createSizeKey(width: Int, height: Int): String {
        return width.toString() + "x" + height
    }

    fun clear() {
        synchronized(sizePools) {
            for (pool in sizePools.values) {
                pool.clear()
            }
            sizePools.clear()
        }
    }

    private class TexturePool(private val width: Int, private val height: Int) {
        private val availableTextures: Queue<Texture> = ArrayDeque<Texture>()
        private val inUseTextures: MutableSet<Texture> = HashSet<Texture>()

        fun acquire(): Texture {
            synchronized(this) {
                var texture = availableTextures.poll()
                if (texture == null) {
                    texture = Texture.create(width, height)
                } else {
                    texture.reset()
                }
                inUseTextures.add(texture)
                return texture
            }
        }

        fun release(texture: Texture) {
            synchronized(this) {
                if (inUseTextures.contains(texture)) {
                    inUseTextures.remove(texture)
                    if (texture.isInvalid()) {
                        texture.delete()
                    } else {
                        availableTextures.offer(texture)
                        while (availableTextures.size > MAX_POOL_SIZE) {
                            val oldest = availableTextures.poll()
                            oldest?.delete()
                        }
                    }
                } else {
                    texture.delete()
                }
            }
        }

        fun clear() {
            synchronized(this) {
                for (texture in availableTextures) {
                    texture.delete()
                }
                availableTextures.clear()

                for (texture in inUseTextures) {
                    texture.delete()
                }
                inUseTextures.clear()
            }
        }
    }
}