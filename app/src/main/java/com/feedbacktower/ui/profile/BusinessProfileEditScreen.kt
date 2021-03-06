package com.feedbacktower.ui.profile

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_profile_setup_screen.*

class BusinessProfileEditScreen : BaseActivity() {

    companion object {
        const val ONBOARDING_KEY = "ONBOARDING_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_profile_setup_screen)
        val navController: NavController = Navigation.findNavController(this, R.id.business_profile_setup_fragment)
        val onboarding = intent?.getBooleanExtra(ONBOARDING_KEY, true)
            ?: throw IllegalArgumentException("Invalid onboading args")
        val bundle = Bundle()
        bundle.putBoolean(ONBOARDING_KEY, onboarding)
        navController.setGraph(navController.graph, bundle)
        NavigationUI.setupWithNavController(toolbar, navController)

    }
}
