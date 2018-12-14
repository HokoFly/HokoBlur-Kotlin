package com.hoko.ktblur.opengl.cache

import java.util.*

abstract class CachePool<K, V>(val maxSize: Int) {

    private val cachedValues: List<V> = LinkedList()

    constructor():this(1024)





}