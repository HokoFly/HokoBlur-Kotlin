package com.hoko.ktblur.api

internal interface Render<T> {
    fun onDrawFrame(t: T)
}