package com.hoko.ktblur.opengl.cache


abstract class CachePool<in K, V>(private val maxSize: Int) {

    private val internalCache: MutableList<V> = ArrayList()

    constructor():this(1024)

    init {
        require(maxSize > 0)
    }

    fun get(key: K): V {
        val removed: V? = remove(key)
        if (removed != null) {
            return removed
        }
        return create(key)
    }

    fun put(value: V) {
        try {
            if (!internalCache.contains(value)) {
                synchronized(this) {
                    if (!internalCache.contains(value)) {
                        internalCache.add(value)
                    }
                }
            }
        } finally {
            trimToSize(maxSize)
        }
    }

    private fun remove(key: K): V? {
        var previous: V? = null
        synchronized(this) {
            val it = internalCache.iterator()
            while (it.hasNext()) {
                val value = it.next()
                if (checkHit(key, value)) {
                    it.remove()
                    previous = value
                    break
                }
            }
        }
        return previous
    }

    fun delete(key: K) {
        val removed: V? = remove(key)
        if (removed != null) {
            entryDeleted(removed)
        }

    }

    protected abstract fun create(key: K): V

    protected abstract fun checkHit(key: K, value: V): Boolean


    private fun trimToSize(maxSize: Int) {
        val removedCollection = ArrayList<V>()
        synchronized(this) {
            while(internalCache.size > maxSize && internalCache.isNotEmpty()) {
                val removed = internalCache.removeAt(0)
                if (removed != null) {
                    removedCollection.add(removed)
                }
            }
        }

        for(removed: V in removedCollection) {
            entryDeleted(removed)
        }
    }

    protected open fun entryDeleted(removed: V) {

    }

    fun maxSize(): Int {
        return maxSize
    }

    fun evictAll() {
        trimToSize(-1)
    }


}