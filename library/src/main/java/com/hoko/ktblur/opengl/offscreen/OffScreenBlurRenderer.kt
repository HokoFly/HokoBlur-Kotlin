package com.hoko.ktblur.opengl.offscreen

import android.graphics.Bitmap
import android.opengl.GLES20
import com.hoko.ktblur.opengl.cache.FrameBufferCache
import com.hoko.ktblur.opengl.FrameBuffer
import com.hoko.ktblur.opengl.Program
import com.hoko.ktblur.opengl.Texture
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.opengl.cache.ProgramManager
import com.hoko.ktblur.opengl.cache.TextureCache
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext

internal class OffScreenBlurRenderer(private val mode: Mode, private val radius: Int) : Render<Bitmap> {

    companion object {
        private val TAG = OffScreenBlurRenderer::class.java.simpleName
        private const val COORDS_PER_VERTEX = 3
        private const val VERTEX_STRIDE = COORDS_PER_VERTEX * 4
    }

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

    private lateinit var mProgram: Program

    private val vertexBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer
    private val texCoordBuffer: FloatBuffer

    init {
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.size * 4).apply { order(ByteOrder.nativeOrder()) }.let {
            it.asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }
        drawListBuffer = ByteBuffer.allocateDirect(drawOrder.size * 2).apply { order(ByteOrder.nativeOrder()) }.let {
            it.asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }
        texCoordBuffer = ByteBuffer.allocateDirect(texHorizontalCoords.size * 4).apply { order(ByteOrder.nativeOrder()) }.let {
            it.asFloatBuffer().apply {
                put(texHorizontalCoords)
                position(0)
            }
        }

        mProgram = ProgramManager.getProgram(mode)
    }

    override fun onDrawFrame(t: Bitmap) {
        if (t.isRecycled) {
            return
        }
        check(t.width > 0 && t.height > 0)
        var blurContext: BlurContext? = null
        try {
            blurContext = prepare(t)
            draw(blurContext)
        } finally {
            onPostBlur(blurContext)
        }
    }

    private fun prepare(bitmap: Bitmap): BlurContext {
        val context = (EGLContext.getEGL() as EGL10).eglGetCurrentContext()
        check(context !== EGL10.EGL_NO_CONTEXT) {
            "This thread has no EGLContext."
        }
        check(this::mProgram.isInitialized && mProgram.id != 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glViewport(0, 0, bitmap.width, bitmap.height)
        return BlurContext(bitmap)

    }

    private fun draw(blurContext: BlurContext) {
        drawOneDimenBlur(blurContext, true)
        drawOneDimenBlur(blurContext, false)
        GLES20.glFinish();
    }

    private fun drawOneDimenBlur(blurContext: BlurContext, isHorizontal: Boolean) {
        try {
            val p = mProgram
            GLES20.glUseProgram(p.id)
            val positionId = GLES20.glGetAttribLocation(p.id, "aPosition")
            GLES20.glEnableVertexAttribArray(positionId)
            GLES20.glVertexAttribPointer(
                positionId,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )
            val texCoordId = GLES20.glGetAttribLocation(p.id, "aTexCoord")
            GLES20.glEnableVertexAttribArray(texCoordId)
            GLES20.glVertexAttribPointer(texCoordId, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)
            if (isHorizontal) {
                blurContext.blurFrameBuffer.bindSelf()
            }
            val textureUniformId = GLES20.glGetUniformLocation(p.id, "uTexture")
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                if (isHorizontal) blurContext.inputTexture.id else blurContext.horizontalTexture.id
            )
            GLES20.glUniform1i(textureUniformId, 0)
            val radiusId = GLES20.glGetUniformLocation(p.id, "uRadius")
            val widthOffsetId = GLES20.glGetUniformLocation(p.id, "uWidthOffset")
            val heightOffsetId = GLES20.glGetUniformLocation(p.id, "uHeightOffset")
            GLES20.glUniform1i(radiusId, radius)
            GLES20.glUniform1f(widthOffsetId, if (isHorizontal) 0f else 1f / blurContext.bitmap.width)
            GLES20.glUniform1f(heightOffsetId, if (isHorizontal) 1f / blurContext.bitmap.height else 0f)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)
            if (!isHorizontal) {
                GLES20.glDisableVertexAttribArray(positionId)
                GLES20.glDisableVertexAttribArray(texCoordId)
            }
        } finally {
            resetAllBuffer()
        }

    }

    private fun resetAllBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUseProgram(0)
        vertexBuffer.rewind()
        texCoordBuffer.rewind()
        drawListBuffer.rewind()
    }

    private fun onPostBlur(blurContext: BlurContext?) {
        blurContext?.finish()
        vertexBuffer.clear()
        texCoordBuffer.clear()
        drawListBuffer.clear()
        releasePrograms()
    }


    private fun releasePrograms() {
        if (this::mProgram.isInitialized) {
            ProgramManager.releaseProgram(mProgram);
        }
    }

    private class BlurContext(val bitmap: Bitmap) {
        val inputTexture: Texture
        val horizontalTexture: Texture
        val blurFrameBuffer: FrameBuffer = FrameBufferCache.getFrameBuffer()

        init {
            inputTexture = TextureCache.acquireTexture(bitmap.getWidth(), bitmap.getHeight())
            inputTexture.uploadBitmap(bitmap)
            horizontalTexture = TextureCache.acquireTexture(bitmap.width, bitmap.height)
            blurFrameBuffer.bindTexture(horizontalTexture)
        }

        fun finish() {
            blurFrameBuffer.unbindTexture()
            TextureCache.releaseTexture(inputTexture)
            TextureCache.releaseTexture(horizontalTexture)
            FrameBufferCache.recycleFrameBuffer(blurFrameBuffer)
        }
    }

}