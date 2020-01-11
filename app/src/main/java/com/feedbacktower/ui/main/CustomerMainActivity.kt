package com.feedbacktower.ui.main

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_customer_main.*

class CustomerMainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)
        val navController = Navigation.findNavController(this, R.id.main_nav_fragment)
        navigation.setupWithNavController(navController)
        NavigationUI.setupWithNavController(toolbar, navController)
    }
}
