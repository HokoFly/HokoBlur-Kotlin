package com.hoko.ktblur.util

import android.opengl.GLES20
import android.util.Log
import com.hoko.ktblur.params.Mode
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext


/**
 * Created by yuxfzju on 16/9/4.
 */
object ShaderUtil {

    private val TAG = ShaderUtil::class.java.simpleName

    val vertexCode: String
        get() {
            val sb = StringBuilder()

            sb.append("uniform mat4 uMVPMatrix;   \n")
                .append("uniform mat4 uTexMatrix;   \n")
                .append("attribute vec2 aTexCoord;   \n")
                .append("attribute vec3 aPosition;  \n")
                .append("varying vec2 vTexCoord;  \n")
                .append("void main() {              \n")
                .append("   gl_Position = uMVPMatrix * vec4(aPosition, 1); \n")
                .append("   vTexCoord = (uTexMatrix * vec4(aTexCoord, 1, 1)).st;\n")
                .append("}  \n")

            return sb.toString()

        }


    /**
     * If set kernel weight array in advance, the GPU registers have no enough space.
     * So compute the weight in the code directly.
     */
    private val gaussianSampleCode: String
        get() {

            val sb = StringBuilder()

            sb.append("   int diameter = 2 * uRadius + 1;  \n")
                .append("   vec4 sampleTex;\n")
                .append("   vec3 col;  \n")
                .append("   float weightSum = 0.0; \n")
                .append("   for(int i = 0; i < diameter; i++) {\n")
                .append("       vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);  \n")
                .append("       sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));\n")
                .append("       float index = float(i); \n")
                .append("       float gaussWeight = getGaussWeight(index - float(diameter - 1)/2.0,")
                .append("           (float(diameter - 1)/2.0 + 1.0) / 2.0); \n")
                .append("       col += sampleTex.rgb * gaussWeight; \n")
                .append("       weightSum += gaussWeight;\n")
                .append("   }   \n")
                .append("   gl_FragColor = vec4(col / weightSum, sampleTex.a);   \n")

            return sb.toString()
        }

    /**
     * If set kernel weight array in advance, the GPU registers have no enough space.
     * So compute the weight in the code directly.
     */
    private val boxSampleCode: String
        get() {
            val sb = StringBuilder()

            sb.append("   int diameter = 2 * uRadius + 1; \n")
                .append("   vec4 sampleTex;\n")
                .append("   vec3 col;  \n")
                .append("   float weightSum = 0.0; \n")
                .append("   for(int i = 0; i < diameter; i++) {\n")
                .append("       vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);  \n")
                .append("        sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));\n")
                .append("       float index = float(i); \n")
                .append("       float boxWeight = float(1.0) / float(diameter); \n")
                .append("       col += sampleTex.rgb * boxWeight; \n")
                .append("       weightSum += boxWeight;\n")
                .append("   }   \n")
                .append("   gl_FragColor = vec4(col / weightSum, sampleTex.a);   \n")
            return sb.toString()
        }

    /**
     * If set kernel weight array in advance, the GPU registers have no enough space.
     * So compute the weight in the code directly.
     */
    private val stackSampleCode: String
        get() {
            val sb = StringBuilder()

            sb.append("int diameter = 2 * uRadius + 1;  \n")
                .append("   vec4 sampleTex;\n")
                .append("   vec3 col;  \n")
                .append("   float weightSum = 0.0; \n")
                .append("   for(int i = 0; i < diameter; i++) {\n")
                .append("       vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);  \n")
                .append("       sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));\n")
                .append("       float index = float(i); \n")
                .append("       float boxWeight = float(uRadius) + 1.0 - abs(index - float(uRadius)); \n")
                .append("       col += sampleTex.rgb * boxWeight; \n")
                .append("       weightSum += boxWeight;\n")
                .append("   }   \n")
                .append("   gl_FragColor = vec4(col / weightSum, sampleTex.a);   \n")

            return sb.toString()
        }

    /**
     * copy the texture
     */
    val copyFragmentCode: String
        get() {
            val sb = StringBuilder()
            sb.append(" \n")
                .append("precision mediump float;")
                .append("varying vec2 vTexCoord;   \n")
                .append("uniform sampler2D uTexture;   \n")
                .append("uniform lowp float mixPercent;   \n")
                .append("uniform vec4 vMixColor;   \n")
                .append("void main() {   \n")
                .append("   vec4 col = vec4(texture2D(uTexture, vTexCoord.st));\n")
                .append("   gl_FragColor = vec4(mix(col.rgb, vMixColor.rgb, vMixColor.a * mixPercent), col.a);   \n")
                .append("}   \n")
            return sb.toString()
        }


    /**
     * get color fragment
     */
    val colorFragmentCode: String
        get() {
            val sb = StringBuilder()
            sb.append("precision mediump float;   \n")
                .append("uniform vec4 vColor;   \n")
                .append("void main() {   \n")
                .append("   gl_FragColor = vColor;   \n")
                .append("} \n")

            return sb.toString()
        }

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

    fun checkEGLContext(): Boolean {
        val context = (EGLContext.getEGL() as EGL10).eglGetCurrentContext()
        if (context == EGL10.EGL_NO_CONTEXT) {
            Log.e(TAG, "This thread is no EGLContext.")
            return false
        } else {
            return true
        }
    }

    fun getFragmentShaderCode(mode: Mode): String {

        val sb = StringBuilder()
        sb.append(" \n")
            .append("precision mediump float;   \n")
            .append("varying vec2 vTexCoord;   \n")
            .append("uniform sampler2D uTexture;   \n")
            .append("uniform int uRadius;   \n")
            .append("uniform float uWidthOffset;  \n")
            .append("uniform float uHeightOffset;  \n")
            .append("mediump float getGaussWeight(mediump float currentPos, mediump float sigma) \n")
            .append("{ \n")
            .append("   return 1.0 / sigma * exp(-(currentPos * currentPos) / (2.0 * sigma * sigma)); \n")
            .append("} \n")

            /**
             * Android 4.4一下系统编译器优化，这里注释暂时不用的GLSL代码
             */
            .append("void main() {   \n")

        if (mode == Mode.BOX) {
            sb.append(ShaderUtil.boxSampleCode)
        } else if (mode == Mode.GAUSSIAN) {
            sb.append(ShaderUtil.gaussianSampleCode)
        } else if (mode == Mode.STACK) {
            sb.append(ShaderUtil.stackSampleCode)
        }
        sb.append("}   \n")

        return sb.toString()
    }

    fun getKernelInitCode(kernel: FloatArray?): String {
        if (kernel == null || kernel.size == 0) {
            return ""
        }

        val sb = StringBuilder("  float kernel[" + kernel.size + "]; \n")

        for (i in kernel.indices) {
            sb.append("  kernel[")
            sb.append(i)
            sb.append("] = ")
            sb.append(kernel[i].toString() + "f; \n")
        }

        return sb.toString()
    }

    fun getOffsetInitCode(radius: Int): String {
        val d = 2 * radius + 1
        val sb = StringBuilder("  vec2 offsets[$d]; \n")

        for (i in -radius..radius) {
            sb.append("  offsets[")
                .append(i + radius)
                .append("] = vec2(")
                .append(i)
                .append(".f * uWidthOffset, ")
                .append(i)
                .append(".f * uHeightOffset); \n")
        }

        return sb.toString()

    }

    //    public static String getSampleCode(int d) {
    //        StringBuilder sb = new StringBuilder();
    //        sb.append("  vec3 sampleTex[KERNEL_SIZE];\n")
    //                .append("  for(int i = 0; i < KERNEL_SIZE; i++) {\n")
    //                .append("        sampleTex[i] = vec3(texture2D(uTexture, 1.0f - (vTexCoord.st + offsets[i])));\n")
    //                .append("  } \n")
    //                .append("  vec3 col;  \n")
    //                .append("  for(int i = 0; i < KERNEL_SIZE; i++) \n")
    //                .append("        col += sampleTex[i] * kernel[i]; \n")
    //                .append("  gl_FragColor = vec4(col, 1.0);   \n");
    //
    //        return sb.toString().replace("KERNEL_SIZE", d + "");
    //    }


}
