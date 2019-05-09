package com.feedbacktower.utilities.cropper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feedbacktower.R
import kotlinx.android.synthetic.main.activity_my_cropper.*

class MyCropperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cropper)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cropImageView.setAspectRatio(1, 1)
        rotate.setOnClickListener { cropImageView.rotateImage(90) }
        flip.setOnClickListener { cropImageView.flipImageHorizontally() }
        done.setOnClickListener { }
    }

}
