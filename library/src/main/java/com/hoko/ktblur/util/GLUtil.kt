package com.hoko.ktblur.util

import android.opengl.GLES20
import android.util.Log

private const val TAG = "GLUtil"
/**
 * return true if no GL Error
 */
internal fun checkGLError(msg: String): Boolean {
    val error = GLES20.glGetError()
    if (error != 0) {
        Log.e(TAG, "checkGLError: error=$error, msg=$msg")
    }
    return error == 0
}

internal fun checkGlState(stage: String?) {
    val error = GLES20.glGetError()
    if (error != GLES20.GL_NO_ERROR) {
        Log.e(TAG, "OpenGL error at " + stage + ": " + getGLErrorString(error))
    }
}

internal fun getGLErrorString(error: Int): String {
    when (error) {
        GLES20.GL_INVALID_ENUM -> return "GL_INVALID_ENUM"
        GLES20.GL_INVALID_VALUE -> return "GL_INVALID_VALUE"
        GLES20.GL_INVALID_OPERATION -> return "GL_INVALID_OPERATION"
        GLES20.GL_OUT_OF_MEMORY -> return "GL_OUT_OF_MEMORY"
        else -> return "Unknown error: $error"
    }
}

internal fun checkFramebufferStatus() {
    val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
    if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
        val statusStr: String?
        when (status) {
            GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> statusStr = "Incomplete attachment"
            GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS -> statusStr = "Incomplete dimensions"
            GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> statusStr = "Missing attachment"
            GLES20.GL_FRAMEBUFFER_UNSUPPORTED -> statusStr = "Unsupported"
            else -> statusStr = "Unknown: $status"
        }
        throw IllegalStateException("Framebuffer not complete: $statusStr")
    }
}