package com.hoko.ktblur.demo

import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.hoko.ktblur.demo.view.DragBlurringView


class DynamicBlurActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_blur)
        val blurredView: RelativeLayout = findViewById(R.id.container)
        val dragBlurringView: DragBlurringView = findViewById(R.id.blurring)
        dragBlurringView.blurredView = blurredView
    }
}
