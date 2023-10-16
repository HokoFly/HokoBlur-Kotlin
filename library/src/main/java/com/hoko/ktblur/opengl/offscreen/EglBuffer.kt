package com.hoko.ktblur.opengl.offscreen

import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Log
import com.hoko.ktblur.params.Mode
import java.nio.IntBuffer
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL10.GL_RGBA
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE



internal class EglBuffer {
    companion object {
        private val TAG = EglBuffer::class.java.simpleName
        private const val EGL_CONTEXT_CLIENT_VERSION: Int = 0x3098
        private const val EGL_OPENGL_ES2_BIT: Int = 4
        private val EGL: EGL10 = EGLContext.getEGL() as EGL10
        private val CONFIG_ATTRIB_LIST = intArrayOf(
            EGL10.EGL_BUFFER_SIZE, 32,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
            EGL10.EGL_NONE
        )
        private val CONTEXT_ATTRIB_LIST: IntArray = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)


    }

    fun getBlurBitmap(bitmap: Bitmap, radius: Int, mode: Mode): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        var eglDisplay = EGL10.EGL_NO_DISPLAY
        var eglSurface: EGLSurface? = null
        var eglContext: EGLContext? = null
        val eglConfigs = arrayOfNulls<EGLConfig>(1)
        kotlin.runCatching {
            eglDisplay = createDisplay(eglConfigs)
            eglSurface = createSurface(w, h, eglDisplay, eglConfigs)
            eglContext = createEGLContext(eglDisplay, eglConfigs)
            EGL.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
            val renderer = createRenderer(radius, mode)
            renderer.onDrawFrame(bitmap)
            EGL.eglSwapBuffers(eglDisplay, eglSurface)
            convertToBitmap(bitmap)
        }.onFailure { t ->
            Log.e(TAG, "Blur the bitmap error", t)
        }.also {
            destroyEglSurface(eglDisplay, eglSurface)
            destroyEglContext(eglDisplay, eglContext)
        }
        return bitmap
    }

    private fun convertToBitmap(bitmap: Bitmap) {
        val w = bitmap.width
        val h = bitmap.height
        val ib = IntBuffer.allocate(w * h)
        GLES20.glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, ib)
        val ia = ib.array()
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(ia))
    }

    private fun createDisplay(eglConfigs: Array<EGLConfig?>): EGLDisplay? {
        val eglDisplay = EGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        EGL.eglInitialize(eglDisplay, IntArray(2))
        EGL.eglChooseConfig(eglDisplay, CONFIG_ATTRIB_LIST, eglConfigs, 1, IntArray(1))
        return eglDisplay
    }

    private fun createSurface(
        width: Int,
        height: Int,
        eglDisplay: EGLDisplay,
        eglConfigs: Array<EGLConfig?>
    ): EGLSurface? {
        val surfaceAttrs = intArrayOf(
            EGL10.EGL_WIDTH, width,
            EGL10.EGL_HEIGHT, height,
            EGL10.EGL_NONE
        )
        return EGL.eglCreatePbufferSurface(eglDisplay, eglConfigs[0], surfaceAttrs)
    }

    private fun destroyEglSurface(eglDisplay: EGLDisplay, eglSurface: EGLSurface?) {
        EGL.eglMakeCurrent(
            eglDisplay,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )
        if (eglSurface != null) {
            EGL.eglDestroySurface(eglDisplay, eglSurface)
        }
    }

    private fun createEGLContext(
        eglDisplay: EGLDisplay,
        eglConfigs: Array<EGLConfig?>
    ): EGLContext? {
        return EGL.eglCreateContext(
            eglDisplay,
            eglConfigs[0], EGL10.EGL_NO_CONTEXT, CONTEXT_ATTRIB_LIST
        )
    }

    private fun destroyEglContext(eglDisplay: EGLDisplay, eglContext: EGLContext?) {
        EGL.eglDestroyContext(eglDisplay, eglContext)
    }

    private fun createRenderer(radius: Int, mode: Mode): OffScreenBlurRenderer {
        val renderer = OffScreenBlurRenderer()
        renderer.radius = radius
        renderer.mode = mode
        return renderer
    }
}