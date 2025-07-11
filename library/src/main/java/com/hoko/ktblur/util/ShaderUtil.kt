package com.hoko.ktblur.util

import com.hoko.ktblur.api.Mode


/**
 * Created by yuxfzju on 16/9/4.
 */

internal val vertexShaderCode: String = """
        attribute vec2 aTexCoord;
        attribute vec4 aPosition;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()


/**
 * If set kernel weight array in advance, the GPU registers have no enough space.
 * So compute the weight in the code directly.
 */
private val gaussianSampleCode: String = """
        int diameter = 2 * uRadius + 1;
        vec4 sampleTex;
        vec3 col;
        float weightSum = 0.0;
        for(int i = 0; i < diameter; i++) {
            vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);
            sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));
            float index = float(i);
            float gaussWeight = getGaussWeight(index - float(diameter - 1)/2.0, (float(diameter - 1)/2.0 + 1.0) / 2.0);
            col += sampleTex.rgb * gaussWeight;
            weightSum += gaussWeight;
        }
        gl_FragColor = vec4(col / weightSum, sampleTex.a);
    """.trimIndent()

/**
 * If set kernel weight array in advance, the GPU registers have no enough space.
 * So compute the weight in the code directly.
 */
private val boxSampleCode: String = """
        int diameter = 2 * uRadius + 1;
        vec4 sampleTex;
        vec3 col;
        float weightSum = 0.0;
        for(int i = 0; i < diameter; i++) {
            vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);
            sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));
            float index = float(i);
            float boxWeight = float(1.0) / float(diameter);
            col += sampleTex.rgb * boxWeight;
            weightSum += boxWeight;
        }
        gl_FragColor = vec4(col / weightSum, sampleTex.a);
    """.trimIndent()


/**
 * If set kernel weight array in advance, the GPU registers have no enough space.
 * So compute the weight in the code directly.
 */
private val stackSampleCode: String = """
        int diameter = 2 * uRadius + 1;
        vec4 sampleTex;
        vec3 col;
        float weightSum = 0.0;
        for(int i = 0; i < diameter; i++) {
            vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);
            sampleTex = vec4(texture2D(uTexture, vTexCoord.st+offset));
            float index = float(i);
            float boxWeight = float(uRadius) + 1.0 - abs(index - float(uRadius));
            col += sampleTex.rgb * boxWeight;
            weightSum += boxWeight;
        }
        gl_FragColor = vec4(col / weightSum, sampleTex.a);
    """.trimIndent()

/**
 * copy the texture
 */
internal val copyFragmentCode: String = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform sampler2D uTexture;
        uniform lowp float mixPercent;
        uniform vec4 vMixColor;
        void main() {
            vec4 col = vec4(texture2D(uTexture, vTexCoord.st));
            gl_FragColor = vec4(mix(col.rgb, vMixColor.rgb, vMixColor.a * mixPercent), col.a);
        }
    """.trimIndent()

internal fun getFragmentShaderCode(mode: Mode): String {

    val sb = StringBuilder(
        """
                precision mediump float;
                varying vec2 vTexCoord;
                uniform sampler2D uTexture;
                uniform int uRadius;
                uniform float uWidthOffset;
                uniform float uHeightOffset;
                mediump float getGaussWeight(mediump float currentPos, mediump float sigma) {
                    return 1.0 / sigma * exp(-(currentPos * currentPos) / (2.0 * sigma * sigma));
                }
                void main() {
            """.trimIndent()
    )

    when (mode) {
        Mode.BOX -> sb.append(boxSampleCode)
        Mode.GAUSSIAN -> sb.append(gaussianSampleCode)
        Mode.STACK -> sb.append(stackSampleCode)
    }
    sb.append("}")

    return sb.toString()
}