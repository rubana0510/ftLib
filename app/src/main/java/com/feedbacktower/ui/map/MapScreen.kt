package com.feedbacktower.ui.map

import android.os.Bundle
import com.feedbacktower.R
import com.feedbacktower.data.models.Location
import com.feedbacktower.ui.base.BaseActivity
import com.feedbacktower.util.zoomToLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapScreen : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var location: Location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_screen)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        location = intent?.getSerializableExtra("LOCATION") as? Location
            ?: throw  IllegalArgumentException("Location cannot be null")
        title = intent?.getStringExtra("TITLE")

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val loc = LatLng(location.latitude!!, location.longitude!!)
        mMap.addMarker(MarkerOptions().position(loc).title("Last location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
        mMap.zoomToLocation(loc, 20f)

    }
}
