package com.hoko.ktblur.opengl.offscreen

internal interface Render<T> {
    fun onDrawFrame(t: T)
}