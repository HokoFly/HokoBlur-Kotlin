package com.hoko.ktblur.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val multiBlurBtn = findViewById<Button>(R.id.multi_blur)
        val dynamicBtn = findViewById<Button>(R.id.dynamic_blur)
        val easyBlurBtn = findViewById<Button>(R.id.easy_blur)
        multiBlurBtn.setOnClickListener(this)
        dynamicBtn.setOnClickListener(this)
        easyBlurBtn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent = Intent()
        when (view.id) {
            R.id.multi_blur -> intent.setClass(this, MultiBlurActivity::class.java)
            R.id.dynamic_blur -> intent.setClass(this, DynamicBlurActivity::class.java)
            R.id.easy_blur -> intent.setClass(this, EasyBlurActivity::class.java)
        }
        val componentName = intent.resolveActivity(packageManager)
        if (componentName != null) {
            startActivity(intent)
        }
    }

}
