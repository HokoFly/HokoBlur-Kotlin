package com.hoko.ktblur.api

internal interface Texture {
    val width: Int

    val height: Int

    val id: Int

    fun create()

    fun delete()
}