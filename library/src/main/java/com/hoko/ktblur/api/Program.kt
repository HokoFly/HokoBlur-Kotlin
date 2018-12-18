package com.hoko.ktblur.api

interface Program {
    fun create(vertexShaderCode: String, fragmentShaderCode: String)

    fun delete()

    fun id(): Int
}