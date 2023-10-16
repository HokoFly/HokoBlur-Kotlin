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
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.api.Scheme
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
    private lateinit var mInBitmap: Bitmap
    private var mRadius = INIT_RADIUS
    private val mAnimator: ValueAnimator by lazy {
        ValueAnimator.ofInt(0, (mRadius / 25f * 1000).toInt()).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                mSeekBar.progress = animation.animatedValue as Int
                updateImage((animation.animatedValue as Int / 1000f * 25f).toInt())
            }
            duration = 300
        }
    }
    private val mRoundAnimator: ValueAnimator by lazy {
        ValueAnimator.ofInt(0, 1000, 0).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                mSeekBar.progress = animation.animatedValue as Int
                val radius = (animation.animatedValue as Int / 1000f * 25).toInt()
                updateImage(radius)
            }
            duration = 2000
        }
    }
    @Volatile
    private var mFuture: Future<*>? = null
    private val mDispatcher = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_blur)
        supportActionBar?.hide()
        mImageView = findViewById(R.id.photo)
        mImageView.setOnClickListener(this)
        mSeekBar = findViewById(R.id.radius_seekbar)
        mSeekBar.setOnSeekBarChangeListener(this)
        mRadiusText = findViewById(R.id.blur_radius)

        findViewById<Spinner>(R.id.scheme_spinner).apply {
            adapter = makeSpinnerAdapter(R.array.blur_schemes)
            onItemSelectedListener = this@MultiBlurActivity
        }
        findViewById<Spinner>(R.id.mode_spinner).apply {
            adapter = makeSpinnerAdapter(R.array.blur_modes)
            onItemSelectedListener = this@MultiBlurActivity
        }
        findViewById<Button>(R.id.reset_btn).apply {
            setOnClickListener(this@MultiBlurActivity)
        }
        findViewById<Button>(R.id.anim_btn).apply {
            setOnClickListener(this@MultiBlurActivity)
        }

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
                mAnimator.start()
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
        mProcessor = mBlurBuilder.processor().apply { radius = mRadius }
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
                mRoundAnimator.start()
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
        mAnimator.let {
            if (it.isStarted) {
                it.end()
            }
        }
        mRoundAnimator.let {
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
        private val bitmap: Bitmap,
        private val blurProcessor: BlurProcessor?,
        private val radius: Int,
        private val onBlurSuccess: (bitmap: Bitmap) -> Unit
    ) : Runnable {

        override fun run() {
            blurProcessor?.let {
                if (!bitmap.isRecycled) {
                    it.radius = radius
                    onBlurSuccess.invoke(it.blur(bitmap))
                }
            }
        }

    }

}
