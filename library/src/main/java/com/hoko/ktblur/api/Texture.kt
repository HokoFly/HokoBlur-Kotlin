package com.hoko.ktblur.api

interface Texture {

    fun create()

    fun delete()

    fun id(): Int

    fun width(): Int

    fun height(): Int

}