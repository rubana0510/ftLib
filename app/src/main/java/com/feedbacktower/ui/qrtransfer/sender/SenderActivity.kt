package com.feedbacktower.ui.qrtransfer.sender

import android.os.Bundle
import androidx.navigation.Navigation
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseActivity

class SenderActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)
        val navController = Navigation.findNavController(this, R.id.sender_nav_fragment)

    }
}
