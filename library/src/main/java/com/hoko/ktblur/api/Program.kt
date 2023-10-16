package com.hoko.ktblur.api

internal interface Program {
    var id: Int

    fun create(vertexShaderCode: String, fragmentShaderCode: String)

    fun delete()
}