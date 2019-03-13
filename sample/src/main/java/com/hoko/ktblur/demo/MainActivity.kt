package com.hoko.ktblur.demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.hoko.ktblur.HokoBlur
import com.hoko.ktblur.params.Mode
import com.hoko.ktblur.params.Scheme
import com.hoko.ktblur.task.AsyncBlurTask

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.image)

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.sample1)
//
//        HokoBlur.with(this).scheme(Scheme.OPENGL).mode(Mode.GAUSSIAN).asyncBlur(bitmap, object : AsyncBlurTask.Callback {
//            override fun onSuccess(bitmap: Bitmap?) {
////                imageView.setImageBitmap(bitmap)
//            }
//
//            override fun onFailed(error: Throwable?) {
//            }
//
//        })

        imageView.setImageBitmap(bitmap)
        imageView.post {
            HokoBlur.with(this).scheme(Scheme.NATIVE).mode(Mode.GAUSSIAN).asyncBlur(imageView, object : AsyncBlurTask.Callback {
                override fun onSuccess(bitmap: Bitmap?) {
                    imageView.setImageBitmap(bitmap)
                }

                override fun onFailed(error: Throwable?) {
                }

            })
        }


//        // sync blur bitmap
//        imageView.setImageBitmap(HokoBlur.with(this).scheme(Scheme.OPENGL).mode(Mode.GAUSSIAN).blur(bitmap))
//
//        // sync blur view
//        imageView.setImageBitmap(bitmap)
//        imageView.post {
//            imageView.setImageBitmap(HokoBlur.with(this).scheme(Scheme.OPENGL).mode(Mode.GAUSSIAN).blur(imageView))
//
//        }

        // Example of a call to a native method
//        sample_text.text = stringFromJNI()

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
//            System.loadLibrary("hoko_blur")
        }
    }
}
