package com.ignitedminds.androidgps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ignitedminds.androidgps.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    companion object {
        val DEFAULT_UPDATE_INTERVAL = 30L
        val DEFAULT_FAST_INTERVAL = 5L
    }

    lateinit var binding: ActivityMainBinding

    //Uses Different Sources To Get The User Location ike GPS,Tower and Wifi
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var currentLocation: Location
    lateinit var locationCallback: LocationCallback

    //Location Request Is A Config File For All Setting Related Tp FusedLocationProvider Client
    val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = 1000 * DEFAULT_UPDATE_INTERVAL
            fastestInterval = 1000 * DEFAULT_FAST_INTERVAL
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            maxWaitTime = 100
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) {
        if (it) {
            updateGPS()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Permission Denied")
                .setMessage("You need to grant access to location to use this app")
                .setPositiveButton("Ok") { dialog, _ ->
                    finish()
                }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                updateUIValues(locationResult.lastLocation)
            }
        }
        setListeners()
        setUpBroadcastReceiver()
        requestLocationPermission()
    }

    @SuppressLint("MissingPermission")
    private fun setListeners() {
        binding.apply {

            btnNewWaypoint.setOnClickListener {
                //Add New Waypoint To The List
                App.getInstance().addLocation(currentLocation)
                binding.tvWaypoints.text = App.getInstance().getLocations().size.toString()
            }
            btnWaypointList.setOnClickListener {
                // Show All Of The Waypoints
                val intent = Intent(this@MainActivity,SavedListLocation::class.java)
                startActivity(intent)
            }

            btnMap.setOnClickListener {
                val intent = Intent(this@MainActivity,MapsActivity::class.java)
                startActivity(intent)
            }


            swGps.setOnClickListener {
                if (swGps.isChecked) {
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    tvSensor.text = "Using GPS Sensors"
                } else {
                    locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                    tvSensor.text = "Tower + Wifi"
                }
            }


            swLocationsupdates.setOnClickListener {
                if (swLocationsupdates.isChecked) {
                    tvUpdates.text = "On"
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null
                    )
                } else {
                    tvUpdates.text = "Off"
                    tvLat.text = "Not Available"
                    tvLon.text = "Not Available"
                    tvAccuracy.text = "Not Available"
                    tvAltitude.text = "Not Available"
                    tvSpeed.text = "Not Available"
                    tvAddress.text = "Not Available"
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }

    }


    private fun setUpBroadcastReceiver() {
        this.registerReceiver(
            GPSStatusReciever(),
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                updateGPS()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Permission Required")
                    .setMessage("This permission is needed to track your location ")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }.setPositiveButton("OK") { dialog, which ->
                        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        dialog.dismiss()
                    }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            updateUIValues(it)
        }
    }

    private fun updateUIValues(location: Location?) {
        if (location != null) {
            currentLocation = location
            binding.apply {

                tvLat.text = location.latitude.toString()
                tvLon.text = location.longitude.toString()
                tvAccuracy.text = location.accuracy.toString()
                tvAltitude.text =
                    if (location.hasAltitude()) location.altitude.toString() else "Not Available"
                tvSpeed.text =
                    if (location.hasSpeed()) location.speed.toString() else "Not Available"
                val geoCoder = Geocoder(this@MainActivity)
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    tvAddress.text = addresses[0].getAddressLine(0)
                } catch (e: Exception) {
                    tvAddress.text = "${e.message}"
                }
            }
        }
    }
}