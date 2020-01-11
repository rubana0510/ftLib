package com.feedbacktower.ui.qrtransfer.receiver

import android.os.Bundle
import androidx.navigation.Navigation
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseActivity

class ReceiverActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
        val navController = Navigation.findNavController(this, R.id.receiver_nav_fragment)

    }
}
