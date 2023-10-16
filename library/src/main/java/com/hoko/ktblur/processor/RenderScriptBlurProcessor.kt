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
import com.hoko.ktblur.util.clamp

internal class RenderScriptBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {
    companion object {
        private val TAG = RenderScriptBlurProcessor::class.java.simpleName
        private const val RS_MAX_RADIUS = 25
    }

    private var renderScript: RenderScript = RenderScript.create(builder.context)
    private var gaussianBlurScript: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    private var boxBlurScript: ScriptC_BoxBlur = ScriptC_BoxBlur(renderScript)
    private var stackBlurScript: ScriptC_StackBlur = ScriptC_StackBlur(renderScript)


    override fun realBlur(bitmap: Bitmap, parallel: Boolean): Bitmap {

        val allocationIn = Allocation.createFromBitmap(renderScript, bitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, Bitmap.createBitmap(bitmap))
        kotlin.runCatching {
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
        }.onFailure { t ->
            Log.e(TAG, "Blur the bitmap error", t)
        }.also {
            allocationIn.destroy()
            allocationOut.destroy()
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
        radius = radius.clamp(0, RS_MAX_RADIUS)
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