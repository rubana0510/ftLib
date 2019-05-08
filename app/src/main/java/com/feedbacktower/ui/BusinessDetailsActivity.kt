package com.feedbacktower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.feedbacktower.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_business_details.*
import org.jetbrains.anko.toast

class BusinessDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_details)
        val navController = Navigation.findNavController(this, R.id.business_detail_nav_fragment)
        if(intent != null && intent.getStringExtra("businessId").isNullOrEmpty()){
            toast("No data passed")
            finish()
            return
        }
        val bundle = Bundle()
        bundle.putString("businessId",intent.getStringExtra("businessId"))
        navController.setGraph(navController.graph,bundle)
    }
}
