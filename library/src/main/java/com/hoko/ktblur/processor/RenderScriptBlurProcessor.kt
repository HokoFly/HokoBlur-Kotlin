package com.hoko.ktblur.processor

import android.graphics.Bitmap
import android.util.Log
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.renderscript.ScriptC_BoxBlur
import com.hoko.ktblur.renderscript.ScriptC_StackBlur

internal class RenderScriptBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {
    companion object {
        private val TAG = RenderScriptBlurProcessor::class.java.simpleName
        private const val RS_MAX_RADIUS = 25
    }

    private val renderScript: RenderScript by lazy { RenderScript.create(builder.context) }
    private val gaussianBlurScript: ScriptIntrinsicBlur by lazy { ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)) }
    private val boxBlurScript: ScriptC_BoxBlur by lazy { ScriptC_BoxBlur(renderScript) }
    private val stackBlurScript: ScriptC_StackBlur by lazy { ScriptC_StackBlur(renderScript) }


    override fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap {

        var allocationIn: Allocation? = null
        var allocationOut: Allocation? = null
       try {
            allocationIn = Allocation.createFromBitmap(renderScript, bitmap)
            allocationOut = Allocation.createFromBitmap(renderScript, Bitmap.createBitmap(bitmap))
            when (mode) {
                Mode.BOX -> {
                    doBoxBlur(bitmap, allocationIn, allocationOut)
                    allocationIn.copyTo(bitmap)
                }
                Mode.STACK -> {
                    doStackBlur(bitmap, allocationIn, allocationOut)
                    allocationIn.copyTo(bitmap)
                }
                Mode.GAUSSIAN -> {
                    doGaussianBlur(allocationIn, allocationOut)
                    allocationOut.copyTo(bitmap)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Blur the bitmap error", t)
        } finally {
           allocationIn?.destroy()
           allocationOut?.destroy()
        }
        return bitmap

    }


    private fun doBoxBlur(bitmap: Bitmap, input: Allocation, output: Allocation) {
        boxBlurScript.apply {
            _input = input
            _output = output
            _width = bitmap.width
            _height = bitmap.height
            _radius = radius
        }.forEach_boxblur_h(input)

        boxBlurScript.apply {
            _input = output
            _output = input
        }.forEach_boxblur_v(output)

    }

    private fun doGaussianBlur(input: Allocation, output: Allocation) {
        // RenderScript won't work, if too large blur radius
        radius = radius.coerceIn(0, RS_MAX_RADIUS)
        gaussianBlurScript.apply {
            setRadius(radius.toFloat())
            setInput(input)
        }.forEach(output)
    }

    private fun doStackBlur(bitmap: Bitmap, input: Allocation, output: Allocation) {
        stackBlurScript.apply {
            _input = input
            _output = output
            _width = bitmap.width
            _height = bitmap.height
            _radius = radius
        }.forEach_stackblur_v(input)

        stackBlurScript.apply {
            _input = output
            _output = input
        }.forEach_stackblur_h(output)
    }
}