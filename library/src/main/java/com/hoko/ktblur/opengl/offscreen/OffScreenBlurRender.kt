package com.hoko.ktblur.opengl.offscreen

import android.graphics.Bitmap
import com.hoko.ktblur.api.Render

class OffScreenBlurRender : Render<Bitmap> {
    //todo 使用原始字符串
    private val vertexShaderCode = "attribute vec2 aTexCoord;   \n" +
            "attribute vec4 aPosition;  \n" +
            "varying vec2 vTexCoord;  \n" +
            "void main() {              \n" +
            "  gl_Position = aPosition; \n" +
            "  vTexCoord = aTexCoord; \n" +
            "}  \n"

    private val COORDS_PER_VERTEX = 3
    private val VERTEX_STRIDE = COORDS_PER_VERTEX * 4

    private val squareCoords = floatArrayOf(
        -1f, 1f, 0.0f, // top left
        -1f, -1f, 0.0f, // bottom left
        1f, -1f, 0.0f, // bottom right
        1f, 1f, 0.0f    // top right
    )

    private val texHorizontalCoords = floatArrayOf(
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 0.0f,
        0.0f, 1.0f
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    override fun onDrawFrame(t: Bitmap) {


    }
}