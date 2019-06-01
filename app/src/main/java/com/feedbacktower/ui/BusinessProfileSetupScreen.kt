package com.feedbacktower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.feedbacktower.R
import kotlinx.android.synthetic.main.activity_profile_setup_screen.*

class BusinessProfileSetupScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_profile_setup_screen)
        val navController = Navigation.findNavController(this, R.id.business_profile_setup_fragment)
        NavigationUI.setupWithNavController(toolbar, navController)
    }
}
