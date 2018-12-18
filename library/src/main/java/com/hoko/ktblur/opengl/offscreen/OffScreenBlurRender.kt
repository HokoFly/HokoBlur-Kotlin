package com.hoko.ktblur.opengl.offscreen

import android.graphics.Bitmap
import android.opengl.GLES20
import com.hoko.ktblur.api.FrameBuffer
import com.hoko.ktblur.api.Program
import com.hoko.ktblur.api.Render
import com.hoko.ktblur.api.Texture
import com.hoko.ktblur.opengl.cache.FrameBufferCache
import com.hoko.ktblur.opengl.program.ProgramFactory
import com.hoko.ktblur.opengl.texture.TextureFactory
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.util.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext

class OffScreenBlurRender : Render<Bitmap> {

    companion object {
        private val TAG = OffScreenBlurRender::class.java.simpleName

    }

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

    private var mProgram: Program? = null

    private val vertexBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer
    private val texCoordBuffer: FloatBuffer

    private var radius: Int = 0
    private var mode: Mode = Mode.STACK

    @Volatile
    private var needRelink: Boolean = true


    init {
        val bb = ByteBuffer.allocateDirect(squareCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        val tcb = ByteBuffer.allocateDirect(texHorizontalCoords.size * 4)
        tcb.order(ByteOrder.nativeOrder())
        texCoordBuffer = tcb.asFloatBuffer()
        texCoordBuffer.put(texHorizontalCoords)
        texCoordBuffer.position(0)

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
        if (context == EGL10.EGL_NO_CONTEXT) {
            throw IllegalStateException("This thread has no EGLContext.")
        }

        if (needRelink) {
            deletePrograms()
            mProgram = ProgramFactory.create(vertexShaderCode, ShaderUtil.getFragmentShaderCode(mode))
            needRelink = false
        }

        mProgram?.id() ?: throw IllegalStateException("Failed to create program.")

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glViewport(0, 0, bitmap.width, bitmap.height)

        return BlurContext(bitmap)

    }

    private fun draw(blurContext: BlurContext) {
        drawOneDimenBlur(blurContext, true)
        drawOneDimenBlur(blurContext, false)

    }

    private fun drawOneDimenBlur(blurContext: BlurContext, isHorizontal: Boolean) {
        try {
            val p = mProgram ?: return

            GLES20.glUseProgram(p.id())

            val positionId = GLES20.glGetAttribLocation(p.id(), "aPosition")
            GLES20.glEnableVertexAttribArray(positionId)
            GLES20.glVertexAttribPointer(
                positionId,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                vertexBuffer
            )

            //        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            //        GLES20.glUniform4fv(mColorHandle, 1, fragmentColor, 0);

            val texCoordId = GLES20.glGetAttribLocation(p.id(), "aTexCoord")
            GLES20.glEnableVertexAttribArray(texCoordId)
            GLES20.glVertexAttribPointer(texCoordId, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

            if (isHorizontal) {
                blurContext.blurFrameBuffer.bindSelf()
            }

            val textureUniformId = GLES20.glGetUniformLocation(p.id(), "uTexture")
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                if (isHorizontal) blurContext.inputTexture.id() else blurContext.horizontalTexture.id()
            )
            GLES20.glUniform1i(textureUniformId, 0)

            val radiusId = GLES20.glGetUniformLocation(p.id(), "uRadius")
            val widthOffsetId = GLES20.glGetUniformLocation(p.id(), "uWidthOffset")
            val heightOffsetId = GLES20.glGetUniformLocation(p.id(), "uHeightOffset")
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
    }


    private fun deletePrograms() {
        mProgram?.delete()
    }

    fun free() {
        needRelink = true
        deletePrograms()
    }

    internal fun setBlurMode(mode: Mode) {
        needRelink = true
        this.mode = mode
    }

    internal fun setBlurRadius(radius: Int) {
        this.radius = radius
    }

    private class BlurContext(internal val bitmap: Bitmap) {
        internal val inputTexture: Texture = TextureFactory.create(bitmap)
        internal val horizontalTexture: Texture = TextureFactory.create(bitmap.width, bitmap.height)
        internal val blurFrameBuffer: FrameBuffer = FrameBufferCache.getFrameBuffer()

        init {
            blurFrameBuffer.bindTexture(horizontalTexture)
        }

        internal fun finish() {
            this.inputTexture.delete()
            this.horizontalTexture.delete()
            FrameBufferCache.recycleFrameBuffer(blurFrameBuffer)
        }
    }

}