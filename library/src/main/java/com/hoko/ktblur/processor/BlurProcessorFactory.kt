package com.hoko.ktblur.processor

import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.Scheme

internal class BlurProcessorFactory {
    companion object {
        fun getBlurProcessor(scheme: Scheme, builder: HokoBlurBuild): BlurProcessor {
            return when(scheme) {
                Scheme.RENDERSCRIPT -> RenderScriptBlurProcessor(builder)
                Scheme.OPENGL -> OpenGLBlurProcessor(builder)
                Scheme.NATIVE -> NativeBlurProcessor(builder)
                Scheme.KOTLIN -> OriginBlurProcessor(builder)
            }
        }
    }
}