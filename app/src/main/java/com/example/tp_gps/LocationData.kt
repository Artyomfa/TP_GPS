package com.example.tp_gps

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationData {
    val location: MutableLiveData<Location> = MutableLiveData()
}