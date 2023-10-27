package com.hoko.ktblur.demo

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hoko.ktblur.HokoBlur
import com.hoko.ktblur.api.BlurBuild
import com.hoko.ktblur.api.BlurProcessor
import com.hoko.ktblur.api.Mode
import com.hoko.ktblur.api.Scheme
import com.hoko.ktblur.demo.vm.MultiBlurViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MultiBlurActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    companion object {
        private const val SAMPLE_FACTOR = 8.0f
        private val TEST_IMAGE_RES = intArrayOf(R.drawable.sample1, R.drawable.sample2)
    }

    private var mCurrentImageRes = TEST_IMAGE_RES[0]
    private lateinit var mSeekBar: SeekBar
    private lateinit var mRadiusText: TextView
    private lateinit var mImageView: ImageView
    private val mBlurBuilder: BlurBuild by lazy {
        HokoBlur.with(this).sampleFactor(SAMPLE_FACTOR)
    }
    private val viewModel: MultiBlurViewModel by viewModels()
    private lateinit var mProcessor: BlurProcessor
    private val mSwitchAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, viewModel.blurRadius / 25f).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                mSeekBar.progress = (animation.animatedValue as Float * 100).toInt()
                updateImage((animation.animatedValue as Float * 25f).toInt())
            }
            duration = 300
        }
    }
    private val mRangeAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0.2f, 1.0f, 0.2f).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                mSeekBar.progress = (animation.animatedValue as Float * 100).toInt()
                val radius = (animation.animatedValue as Float * 25).toInt()
                updateImage(radius)
            }
            duration = 2000
        }
    }

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
        viewModel.bitmapLiveData.observe(this) {
            mImageView.setImageBitmap(it)
            endAnimators()
            mSwitchAnimator.start()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.blurFlow.collect { op ->
                mProcessor.radius = op.radius
                val bitmap = viewModel.bitmapLiveData.value
                val blurResult = withContext(Dispatchers.IO) {
                    if (bitmap?.isRecycled?.not() == true) {
                        mProcessor.blur(bitmap)
                    } else {
                        null
                    }
                }
                mImageView.setImageBitmap(blurResult)
            }
        }
        mProcessor = mBlurBuilder.scheme(Scheme.RENDERSCRIPT).mode(Mode.GAUSSIAN).processor()
        setImage(mCurrentImageRes)

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
        viewModel.setImage(id, resources)
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
        mProcessor = mBlurBuilder.processor().apply { radius = viewModel.blurRadius }
        updateImage(viewModel.blurRadius)

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
                mRangeAnimator.start()
            }
            R.id.photo -> {
                mCurrentImageRes = TEST_IMAGE_RES[++viewModel.resIndex % TEST_IMAGE_RES.size]
                setImage(mCurrentImageRes)
            }
        }


    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val radius = (progress / 100f * 25).toInt()
        mRadiusText.text = "Blur Radius: $radius"
        updateImage(radius)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    private fun updateImage(radius: Int) {
        viewModel.changeBlurRadius(radius)
    }

    private fun endAnimators() {
        mSwitchAnimator.let {
            if (it.isStarted) {
                it.end()
            }
        }
        mRangeAnimator.let {
            if (it.isStarted) {
                it.end()
            }
        }
    }
}
