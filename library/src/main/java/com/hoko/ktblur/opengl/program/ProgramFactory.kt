package com.hoko.ktblur.opengl.program

import com.hoko.ktblur.api.Program

object ProgramFactory {

    fun create(vertexShaderCode: String, fragmentShaderCode: String): Program {
        return SimpleProgram(vertexShaderCode, fragmentShaderCode)
    }
}