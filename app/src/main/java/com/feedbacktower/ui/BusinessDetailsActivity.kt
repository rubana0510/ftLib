package com.feedbacktower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.feedbacktower.R

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
    }
}
