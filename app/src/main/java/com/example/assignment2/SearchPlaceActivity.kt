package com.example.assignment2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment2.Utills.AppUtil
import com.example.assignment2.model.PlaceData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_search_places.*

class SearchPlaceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var currentLocation: Location
    private lateinit var placeList: List<PlaceData>
    private lateinit var currentPlaceList: List<PlaceData>
    lateinit var context: Context
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_places)
        context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions()
        } else {
            performCreate()
        }
//        performCreate()
    }

    private fun checkAndRequestPermissions() {
        val permissionList: List<String> = getRequestedPermission()
        val listPermissionsNeeded: MutableList<String> =
            java.util.ArrayList()
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(permission)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                1
            )
        } else {
            performCreate()
        }
    }

    private fun getRequestedPermission(): List<String> {
        val requestedPermission: MutableList<String> = java.util.ArrayList()
        requestedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        requestedPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        requestedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return requestedPermission
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                checkPermission(permissions as Array<String>, grantResults)
            }
            else -> {
            }
        }
    }

    private fun checkPermission(
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        for (counter in permissions.indices) {
            if (grantResults[counter] == PackageManager.PERMISSION_GRANTED) {
                if (counter + 1 == permissions.size) {
                    performCreate()
                }
            } else {
                Toast.makeText(context, "Allow permission", Toast.LENGTH_SHORT).show()
                finish()
//                checkNeverAskConditionForPermission(permissions[counter])
                break
            }
        }
    }

    private fun performCreate() {
        val dataAvailable = DBController.getInstance(this).isDataAvailable(DBController.TABLE_NAME)
        Log.d("Search", "dataAvailable : $dataAvailable")
        if (!dataAvailable) {
            CreateDBAsyncTask(progressBar).execute(context)
        } else {
            setDataOnUi()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    inner class CreateDBAsyncTask(val progressBar: ProgressBar) :
        AsyncTask<Context, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Context?): String {
            Log.d("Search", "CreateDBAsyncTask started")
            AppUtil.createDatabase(context)
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressBar.visibility = View.GONE
            setDataOnUi()
        }
    }

    private fun setDataOnUi() {
        recycler.layoutManager =
            LinearLayoutManager(this)
        placeList = DBController.getInstance(context).getPlaceList("")
        setAdapter(placeList)
    }

    private fun setAdapter(codeList: List<PlaceData>) {
        currentPlaceList = codeList
        // update distance in place list
        if (DataHandler.getInstance().latitude != null && DataHandler.getInstance().longitude != null) {
            val myLocation = Location("")
            myLocation.latitude = DataHandler.getInstance().latitude
            myLocation.longitude = DataHandler.getInstance().longitude

            currentPlaceList.forEach {
                val targetLocation = Location("")
                targetLocation.latitude = it.latitude
                targetLocation.longitude = it.longitude
                it.distance = myLocation.distanceTo(targetLocation).toDouble()
            }
        } else {
            AppUtil.showToast(context,"Turn On GPS to See current location data.")
        }
        recycler.adapter = PlaceListAdapter(this, codeList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search = menu!!.findItem(R.id.search).actionView as SearchView

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String): Boolean {
                if (p0.isEmpty()) {
                    setAdapter(placeList)
                } else {
                    val searchListByTitle = getSearchListByTitle(p0)
                    setAdapter(searchListByTitle)
                }
                return true
            }

        })

        search.setOnCloseListener(SearchView.OnCloseListener {
            setAdapter(placeList)
            false
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sort_by_distance -> {
                //TODO
                currentPlaceList = currentPlaceList.sortedWith(compareBy({ it.distance }))
                setAdapter(currentPlaceList)
            }
            R.id.sort_by_rating -> {
                //TODO
                currentPlaceList = currentPlaceList.sortedWith(compareBy({ -it.rating }))
                setAdapter(currentPlaceList)
            }
            else -> {
            }
        }
        return true
    }

    private fun getSearchListByTitle(title: String): List<PlaceData> {
        val searchList = ArrayList<PlaceData>()
        for (codeData in placeList) {
            if (codeData.name != null && codeData.name.contains(title, true)) {
                searchList.add(codeData)
            }
        }
        return searchList
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        Log.d("Search", "onMapReady mMap.myLocation " + mMap.myLocation)
        mMap.setOnMyLocationChangeListener {
            Log.d("Search", "setOnMyLocationChangeListener " + it)
            if (it != null) {
                DataHandler.getInstance().latitude = it.latitude
                DataHandler.getInstance().longitude = it.longitude
                if (!::currentLocation.isInitialized || currentLocation.distanceTo(it) > 50) {
                    Log.d("Search", "setOnMyLocationChangeListener initialize ne location " + it)
                    currentLocation = it
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                currentLocation.latitude,
                                currentLocation.longitude
                            ), 15f
                        )
                    )

                    if (::currentPlaceList.isInitialized && currentPlaceList.isNotEmpty()) {
                        setAdapter(currentPlaceList)
                    }
//                    setDataOnUi()
                }
            }
        }
    }
}
