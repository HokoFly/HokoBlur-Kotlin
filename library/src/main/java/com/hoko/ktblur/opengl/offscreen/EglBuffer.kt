package com.hoko.ktblur.opengl.offscreen

import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay


private const val EGL_CONTEXT_CLIENT_VERSION: Int = 0x3098
private const val EGL_OPENGL_ES2_BIT: Int = 4

class EglBuffer {

    private val egl: EGL10 = EGLContext.getEGL() as EGL10
    private val eglDisplay: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
    private val eglConfigs: Array<EGLConfig?> = Array(1) {null}
    private val contextAttribs: IntArray = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
    private val threadEGLContext = ThreadLocal<EGLContext>()

    init {
        val configAttribs = intArrayOf(
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
        egl.eglChooseConfig(eglDisplay, configAttribs, eglConfigs, 1, IntArray(1))
    }




}