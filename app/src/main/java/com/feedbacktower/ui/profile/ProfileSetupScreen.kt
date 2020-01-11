package com.feedbacktower.ui.profile

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_profile_setup_screen.*

class ProfileSetupScreen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_screen)
        val navController: NavController = Navigation.findNavController(this, R.id.profile_setup_fragment)
        NavigationUI.setupWithNavController(toolbar, navController)
    }
}
