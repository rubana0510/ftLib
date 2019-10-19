package com.feedbacktower.ui.qrtransfer.receiver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import com.feedbacktower.R

class ReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
        val navController = Navigation.findNavController(this, R.id.receiver_nav_fragment)

    }
}
