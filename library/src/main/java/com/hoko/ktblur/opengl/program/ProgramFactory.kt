package com.hoko.ktblur.opengl.program

import com.hoko.ktblur.api.Program

internal class ProgramFactory {
    companion object {
        fun create(vertexShaderCode: String, fragmentShaderCode: String): Program {
            return SimpleProgram(vertexShaderCode, fragmentShaderCode)
        }
    }
}