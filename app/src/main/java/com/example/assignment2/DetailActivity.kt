package com.example.assignment2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps_detail.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setDataOnUI()
    }

    private fun setDataOnUI() {
        val selectedPlace = DataHandler.getInstance().selectedPlace
        tvPlaceName.text = selectedPlace.name
        tvPlaceType.text = selectedPlace.type
        tvRating.text = selectedPlace.rating.toString()
        ratingBar.rating = selectedPlace.rating.toFloat()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(DataHandler.getInstance().selectedPlace.latitude, DataHandler.getInstance().selectedPlace.latitude)
        mMap.addMarker(MarkerOptions().position(sydney).title(DataHandler.getInstance().selectedPlace.name))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,15f))
    }
}
