package com.ignitedminds.androidgps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import kotlin.math.log

class GPSStatusReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action?.equals(LocationManager.PROVIDERS_CHANGED_ACTION) == true) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            Toast.makeText(
                context,
                "Location Turned ${
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER
                    )
                }",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}