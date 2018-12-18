package com.hoko.ktblur.opengl.util

data class Size(val width: Int, val height: Int) {
    constructor(size: Size) : this(size.width, size.height)
}