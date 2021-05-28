package com.ignitedminds.androidgps

import android.app.Application
import android.location.Location

class App : Application() {
    companion object{
        private lateinit var singleton: App
        private lateinit var locationList : ArrayList<Location>

        fun getInstance(): App{
            return singleton
        }
    }

    fun getLocations() : ArrayList<Location>{
        return locationList
    }

    fun addLocation(location: Location){
        locationList.add(location)
    }

    override fun onCreate() {
        super.onCreate()
        singleton = this
        locationList = ArrayList()

    }
}