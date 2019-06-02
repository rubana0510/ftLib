package com.feedbacktower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.feedbacktower.R
import kotlinx.android.synthetic.main.activity_business_details.*

class BusinessDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_details)
        val navController = Navigation.findNavController(this, R.id.business_detail_nav_fragment)
        if(intent != null && intent.getStringExtra("businessId").isNullOrEmpty()){
           throw IllegalArgumentException("Invalid business args")
        }
        val bundle = Bundle()
        bundle.putString("businessId",intent.getStringExtra("businessId"))
        navController.setGraph(navController.graph,bundle)
        NavigationUI.setupWithNavController(toolbar, navController)

    }
}
