package com.hoko.ktblur.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.hoko.ktblur.HokoBlur
import com.hoko.ktblur.api.Scheme


class EasyBlurActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_blur)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cat)

        val imageView = findViewById<ImageView>(R.id.image)
        val imageView1 = findViewById<ImageView>(R.id.image1)
        val imageView2 = findViewById<ImageView>(R.id.image2)
        val imageView3 = findViewById<ImageView>(R.id.image3)

        imageView.setImageBitmap(bitmap)
        imageView1.setImageBitmap(
            HokoBlur.with(this)
                .forceCopy(true)
                .scheme(Scheme.RENDERSCRIPT)
                .sampleFactor(3.0f)
                .radius(20)
                .blur(bitmap)
        )
        HokoBlur.with(this)
            .scheme(Scheme.OPENGL)
            .translateX(150)
            .translateY(150)
            .forceCopy(false)
            .sampleFactor(5.0f)
            .asyncBlur(bitmap) {
                onSuccess {
                    imageView2.setImageBitmap(it)
                }
                onFailed {
                    it?.printStackTrace()
                }
            }

        imageView1.post {
            HokoBlur.with(this)
                .scheme(Scheme.NATIVE)
                .translateX(100)
                .translateY(100)
                .sampleFactor(5.0f)
                .asyncBlur(imageView) {
                    onSuccess {
                        imageView2.setImageBitmap(it)
                    }
                    onFailed {
                        it?.printStackTrace()
                    }
                }
        }

    }
}
