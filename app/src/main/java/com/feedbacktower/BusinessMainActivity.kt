package com.feedbacktower

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_business_main.*

class BusinessMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_main)
        val navController = Navigation.findNavController(this, R.id.main_nav_fragment)
        navigation.setupWithNavController(navController)
        NavigationUI.setupWithNavController(toolbar, navController)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.change_city_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.select_city_id) {
            /*  HomeFragmentDirections.actionNavigationHomeToSelectCityFragment().let {
                  findNavController().navigate(it)
              }*/
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
