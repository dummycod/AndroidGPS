package com.ignitedminds.androidgps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.ignitedminds.androidgps.databinding.ActivitySavedListLocationBinding

class SavedListLocation : AppCompatActivity() {
    lateinit var binding : ActivitySavedListLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_saved_list_location)
        val savedLocations = App.getInstance().getLocations()
        binding.savedLocationsList.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,savedLocations)

    }
}