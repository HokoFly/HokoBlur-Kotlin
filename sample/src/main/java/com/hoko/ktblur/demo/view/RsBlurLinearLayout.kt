package com.hoko.ktblur.demo.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hoko.ktblur.HokoBlur
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.Scheme
import kotlin.math.max


/**
 * Created by yuxfzju on 16/9/18.
 */
class RsBlurLinearLayout : LinearLayout {
    companion object {
        private const val DEFAULT_BLUR_RADIUS = 5
        private const val DEFAULT_BITMAP_SAMPLE_FACTOR = 5.0f
    }

    private var mLocationInWindow: IntArray = IntArray(2)

    private var mProcessor: BlurProcessor

    private var mBitmap: Bitmap? = null

    private var mCanvas: Canvas = Canvas()

    private val mOnPreDrawListener = {
        if (visibility == View.VISIBLE) {
            prepare()
        }
        true
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mProcessor = HokoBlur.with(context).scheme(Scheme.RENDERSCRIPT)
            .sampleFactor(DEFAULT_BITMAP_SAMPLE_FACTOR).processor()
        setBlurRadius(DEFAULT_BLUR_RADIUS)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnPreDrawListener(mOnPreDrawListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnPreDrawListener(mOnPreDrawListener)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mCanvas === canvas) {
            mBitmap = mBitmap?.let { mProcessor.blur(it) }
        } else {
            mBitmap?.let {
                canvas.drawBitmap(it, Matrix(), null)
            }
            super.dispatchDraw(canvas)
        }
    }

    private fun setBlurRadius(radius: Int) {
        mProcessor.radius = radius
        invalidate()
    }

    fun setSampleFactor(factor: Float) {
        mProcessor.sampleFactor = factor
        invalidate()
    }

    private fun prepare() {
        val width = max(width, 1)
        val height = max(height, 1)

        if (mBitmap == null || mBitmap?.width != width || mBitmap?.height != height) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }

        getLocationInWindow(mLocationInWindow)
        with(mCanvas) {
            restoreToCount(1)
            setBitmap(mBitmap)
            setMatrix(Matrix())
            translate((-mLocationInWindow[0]).toFloat(), (-mLocationInWindow[1]).toFloat())
            save()
        }
        rootView.draw(mCanvas)
    }

}