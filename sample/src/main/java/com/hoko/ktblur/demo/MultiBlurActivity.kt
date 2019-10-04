package com.hoko.ktblur.demo

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.hoko.ktblur.HokoBlur
import com.hoko.ktblur.api.BlurBuild
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.params.Scheme
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MultiBlurActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    companion object {
        private const val SAMPLE_FACTOR = 8.0f
        private const val INIT_RADIUS = 5
        private val TEST_IMAGE_RES = intArrayOf(R.drawable.sample1, R.drawable.sample2)
    }

    private var mCurrentImageRes = TEST_IMAGE_RES[0]
    private var index = 0
    private lateinit var mSeekBar: SeekBar
    private lateinit var mRadiusText: TextView
    private lateinit var mImageView: ImageView

    private lateinit var mBlurBuilder: BlurBuild
    private var mProcessor: BlurProcessor? = null
    private var mInBitmap: Bitmap? = null
    private var mRadius = INIT_RADIUS
    private var mAnimator: ValueAnimator? = null
    private var mRoundAnimator: ValueAnimator? = null
    @Volatile
    private var mFuture: Future<*>? = null
    private val mDispatcher = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_blur)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        mImageView = findViewById(R.id.photo)
        mImageView.setOnClickListener(this)
        mSeekBar = findViewById(R.id.radius_seekbar)
        mSeekBar.setOnSeekBarChangeListener(this)
        mRadiusText = findViewById(R.id.blur_radius)

        val schemeSpinner = findViewById<Spinner>(R.id.scheme_spinner)
        schemeSpinner.adapter = makeSpinnerAdapter(R.array.blur_schemes)
        schemeSpinner.onItemSelectedListener = this

        val modeSpinner = findViewById<Spinner>(R.id.mode_spinner)
        modeSpinner.adapter = makeSpinnerAdapter(R.array.blur_modes)
        modeSpinner.onItemSelectedListener = this

        val resetBtn = findViewById<Button>(R.id.reset_btn)
        resetBtn.setOnClickListener(this)
        val animBtn = findViewById<Button>(R.id.anim_btn)
        animBtn.setOnClickListener(this)

        setImage(mCurrentImageRes)
        mBlurBuilder = HokoBlur.with(this).sampleFactor(SAMPLE_FACTOR)

    }

    private fun makeSpinnerAdapter(@ArrayRes arrayRes: Int): SpinnerAdapter {
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            arrayRes, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return spinnerAdapter
    }


    private fun setImage(@DrawableRes id: Int) {
        mImageView.setImageResource(id)
        mDispatcher.submit {
            mInBitmap = BitmapFactory.decodeResource(resources, id)

            runOnUiThread {
                endAnimators()
                mAnimator = ValueAnimator.ofInt(0, (mRadius / 25f * 1000).toInt()).also { animator ->
                        animator.interpolator = LinearInterpolator()
                        animator.addUpdateListener { animation ->
                            mSeekBar.progress = animation.animatedValue as Int
                            updateImage((animation.animatedValue as Int / 1000f * 25f).toInt())
                        }

                        animator.duration = 300
                        animator.start()
                    }

            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val spinnerId = parent.id
        if (spinnerId == R.id.scheme_spinner) {
            when (position) {
                0 -> mBlurBuilder.scheme(Scheme.RENDERSCRIPT)
                1 -> mBlurBuilder.scheme(Scheme.OPENGL)
                2 -> mBlurBuilder.scheme(Scheme.NATIVE)
                3 -> mBlurBuilder.scheme(Scheme.KOTLIN)
            }

        } else if (spinnerId == R.id.mode_spinner) {
            when (position) {
                0 -> mBlurBuilder.mode(Mode.GAUSSIAN)
                1 -> mBlurBuilder.mode(Mode.STACK)
                2 -> mBlurBuilder.mode(Mode.BOX)
            }

        }
        endAnimators()
        mProcessor = mBlurBuilder.processor()
        mProcessor?.radius = mRadius
        updateImage(mRadius)

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.reset_btn -> {
                endAnimators()
                mImageView.setImageResource(mCurrentImageRes)
                mSeekBar.progress = 0
            }
            R.id.anim_btn -> {
                endAnimators()
                mRoundAnimator = ValueAnimator.ofInt(0, 1000, 0).also { animator ->
                    animator.interpolator = LinearInterpolator()
                    animator.addUpdateListener { animation ->
                        mSeekBar.progress = animation.animatedValue as Int
                        val radius = (animation.animatedValue as Int / 1000f * 25).toInt()
                        updateImage(radius)
                    }
                    animator.duration = 2000
                    animator.start()
                }

            }
            R.id.photo -> {
                mCurrentImageRes = TEST_IMAGE_RES[++index % TEST_IMAGE_RES.size]
                setImage(mCurrentImageRes)
            }
        }


    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val radius = (progress / 1000f * 25).toInt()
        mRadiusText.text = "Blur Radius: $radius"
        updateImage(radius)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    private fun updateImage(radius: Int) {
        mRadius = radius
        cancelPreTask()
        mFuture = mDispatcher.submit(BlurTask(mInBitmap, mProcessor, radius) { bitmap ->
            if (!isFinishing) {
                runOnUiThread { mImageView.setImageBitmap(bitmap) }
            }
        })
    }

    private fun cancelPreTask() {
        mFuture?.let {
            if (!it.isCancelled && !it.isDone) {
                it.cancel(false)
            }
        }
    }

    private fun endAnimators() {
        mAnimator?.let {
            if (it.isStarted) {
                it.end()
            }
        }
        mRoundAnimator?.let {
            if (it.isStarted) {
                it.end()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelPreTask()
    }

    private class BlurTask internal constructor(
        private val bitmap: Bitmap?,
        private val blurProcessor: BlurProcessor?,
        private val radius: Int,
        private val onBlurSuccess: (bitmap: Bitmap) -> Unit
    ) : Runnable {

        override fun run() {
            if (bitmap != null && !bitmap.isRecycled && blurProcessor != null) {
                blurProcessor.radius = radius
                onBlurSuccess.invoke(blurProcessor.blur(bitmap))
            }
        }

    }

}
