package com.hoko.ktblur.api

internal interface FrameBuffer {
    fun create()

    fun bindTexture(texture: Texture)

    fun bindSelf()

    fun delete()
}