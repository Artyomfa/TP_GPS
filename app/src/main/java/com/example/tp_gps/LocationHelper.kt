package com.example.tp_gps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi

object LocationHelper: LocationListener {
    private var locationManager: LocationManager? = null
    private var locationUpdater: ((Location)->Unit)? = null
    private lateinit var previousLocation: Location
    public var distance: Double = 0.0
    private val wind = MainActivity()
    var imHere: Location? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun startLocationListening(context: Context, locationUpdater: (Location)-> Unit){
        locationManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            .also {locationManager->
                if (
                    context.checkSelfPermission(ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    500L,
                    10F,
                    this
                )
                this.locationUpdater = locationUpdater
            }
    }
    fun stopLocating(){
        locationManager?.removeUpdates(this)
    }
    override fun onLocationChanged(location: Location){
        locationUpdater?.invoke(location)
        if (location.hasSpeed() && ::previousLocation.isInitialized) {
            distance += previousLocation.distanceTo(location)
        }
        println("Latitude: ${location.latitude}; Longtitude: ${location.longitude}")
        previousLocation = location
        println("distance  " + distance.toString())
        wind.distance = distance
        wind.updateDistance(distance.toString())
        println("maindistance  " + wind.distance.toString())
    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onProviderEnabled(provider: String) {

    }

}