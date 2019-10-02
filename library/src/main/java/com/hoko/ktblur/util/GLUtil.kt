package com.hoko.ktblur.util

import android.opengl.GLES20
import android.util.Log

class GLUtil {
    companion object {
        private val TAG = GLUtil::class.java.simpleName
        /**
         * return true if no GL Error
         */
        fun checkGLError(msg: String): Boolean {
            val error = GLES20.glGetError()
            if (error != 0) {
                Log.e(TAG, "checkGLError: error=$error, msg=$msg")
            }

            return error == 0
        }

    }
}