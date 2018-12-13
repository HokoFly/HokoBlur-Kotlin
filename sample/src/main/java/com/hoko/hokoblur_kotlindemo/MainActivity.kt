package com.hoko.hokoblur_kotlindemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.hoko.ktblur.HokoBlur
import com.hoko.ktblur.params.Mode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.image)

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.sample1)

        imageView.setImageBitmap(HokoBlur.with(this).mode(Mode.STACK).blur(bitmap))


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
