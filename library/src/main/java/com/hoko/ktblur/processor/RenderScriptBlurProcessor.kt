package com.hoko.ktblur.processor

import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import android.util.Log
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.renderscript.ScriptC_BoxBlur
import com.hoko.ktblur.renderscript.ScriptC_StackBlur
import com.hoko.ktblur.util.MathUtil

class RenderScriptBlurProcessor(builder: HokoBlurBuild) : AbstractBlurProcessor(builder) {
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
        try {
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

        } catch (e: Throwable) {
            Log.e(TAG, "Blur the bitmap error", e)
        } finally {
            allocationIn.destroy()
            allocationOut.destroy()
        }

        return bitmap

    }


    private fun doBoxBlur(bitmap: Bitmap, input: Allocation, output: Allocation) {

        boxBlurScript._input = input
        boxBlurScript._output = output
        boxBlurScript._width = bitmap.width
        boxBlurScript._height = bitmap.height
        boxBlurScript._radius = radius
        boxBlurScript.forEach_boxblur_h(input)

        boxBlurScript._input = output
        boxBlurScript._output = input
        boxBlurScript.forEach_boxblur_v(output)

    }

    private fun doGaussianBlur(input: Allocation, output: Allocation) {
        // RenderScript won't work, if too large blur radius
        radius = MathUtil.clamp(radius, 0, RS_MAX_RADIUS)
        gaussianBlurScript.setRadius(radius.toFloat())
        //        mAllocationIn.copyFrom(input);
        gaussianBlurScript.setInput(input)
        gaussianBlurScript.forEach(output)
    }

    private fun doStackBlur(bitmap: Bitmap, input: Allocation, output: Allocation) {

        stackBlurScript._input = input
        stackBlurScript._output = output
        stackBlurScript._width = bitmap.width
        stackBlurScript._height = bitmap.height
        stackBlurScript._radius = radius
        stackBlurScript.forEach_stackblur_v(input)

        stackBlurScript._input = output
        stackBlurScript._output = input
        stackBlurScript.forEach_stackblur_h(output)
    }
}