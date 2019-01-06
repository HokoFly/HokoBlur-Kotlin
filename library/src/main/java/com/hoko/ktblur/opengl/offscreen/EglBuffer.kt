package com.hoko.ktblur.opengl.offscreen

import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Log
import com.hoko.ktblur.params.Mode
import java.nio.IntBuffer
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.opengles.GL10.GL_RGBA
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE



class EglBuffer {
    companion object {
        private val TAG = EglBuffer::class.java.simpleName
        private const val EGL_CONTEXT_CLIENT_VERSION: Int = 0x3098
        private const val EGL_OPENGL_ES2_BIT: Int = 4
    }

    private val egl: EGL10 = EGLContext.getEGL() as EGL10
    private val eglDisplay: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
    private val eglConfigs: Array<EGLConfig?> = Array(1) {null}
    private val contextAttrs: IntArray = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)

    //EGLContext、EGLSurface and Renderer are bound to current thread.
    // So here use the ThreadLocal to implement Thread isolation.
    private val threadRenderer = ThreadLocal<OffScreenBlurRenderer>()
    private val threadEGLContext = ThreadLocal<EGLContext>()

    init {
        val configAttrs = intArrayOf(
            EGL10.EGL_BUFFER_SIZE, 32,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
            EGL10.EGL_NONE
        )

        egl.eglInitialize(eglDisplay, IntArray(2))
        egl.eglChooseConfig(eglDisplay, configAttrs, eglConfigs, 1, IntArray(1))
    }

    fun getBlurBitmap(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        try {
            val eglSurface = createSurface(w, h)
            getRenderer().onDrawFrame(bitmap)
            egl.eglSwapBuffers(eglDisplay, eglSurface)
            convertToBitmap(bitmap)
        } catch (t: Throwable) {
            Log.e(TAG, "Blur the bitmap error", t)
        } finally {
            unbindEglCurrent()
        }

        return bitmap
    }

    private fun createSurface(width: Int, height: Int): EGLSurface {
        val surfaceAttrs = intArrayOf(EGL10.EGL_WIDTH, width, EGL10.EGL_HEIGHT, height, EGL10.EGL_NONE)

        val eglSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfigs[0], surfaceAttrs)

        egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, getEGLContext())

        return eglSurface

    }


    private fun convertToBitmap(bitmap: Bitmap) {
        val w = bitmap.width
        val h = bitmap.height

        val ib = IntBuffer.allocate(w * h)
        GLES20.glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, ib)
        val ia = ib.array()

        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(ia))
    }

    /**
     * When the current thread finish renderring and reading pixels, the EGLContext should be unbound.
     * Then the EGLContext could be reused for other threads. Make it possible to share the EGLContext
     * To bind the EGLContext to current Thread, just call eglMakeCurrent()
     */
    private fun unbindEglCurrent() {
        egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)

    }

    private fun getRenderer(): OffScreenBlurRenderer {
        var renderer: OffScreenBlurRenderer? = threadRenderer.get()
        if (renderer == null) {
            renderer = OffScreenBlurRenderer()
            threadRenderer.set(renderer)
        }

        return renderer
    }

    private fun getEGLContext(): EGLContext? {
        var eglContext: EGLContext? = threadEGLContext.get()
        if (eglContext == null) {
            eglContext = egl.eglCreateContext(eglDisplay, eglConfigs[0], EGL10.EGL_NO_CONTEXT, contextAttrs)
            threadEGLContext.set(eglContext)
        }

        return eglContext
    }


    fun setBlurRadius(radius: Int) {
        getRenderer().setBlurRadius(radius)
    }

    fun setBlurMode(mode: Mode) {
        getRenderer().setBlurMode(mode)
    }

    fun free() {
        getRenderer().free()
    }


}