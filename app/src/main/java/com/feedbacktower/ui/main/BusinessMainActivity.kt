package com.feedbacktower.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.feedbacktower.R
import kotlinx.android.synthetic.main.activity_business_main.*


class BusinessMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_main)
        val navController = Navigation.findNavController(this, R.id.main_nav_fragment)
        navigation.setupWithNavController(navController)
        val mToolbar = toolbar
        mToolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_right_24dp)
        NavigationUI.setupWithNavController(mToolbar, navController)
    }
}
