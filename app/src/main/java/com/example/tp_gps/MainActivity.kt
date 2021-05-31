package com.example.tp_gps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var startStopBtn: FloatingActionButton
    private lateinit var output: LinearLayout
    public var distance: Float = 0.0F

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        var i: Int = 0

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startStopBtn = findViewById<FloatingActionButton>(R.id.startStopBtn).apply {
            setOnClickListener {
                println("Button touched")
                println(i.toString())
                println("distance   " + distance.toString())

                i += 1
                if (
                    context.checkSelfPermission(ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestLocationPermission()
                } else {
                    changeServiceState()
                }
            }

        }
        output = findViewById(R.id.output)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission() {
        this.requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0
            && permissions.first() == ACCESS_FINE_LOCATION
            && grantResults.first() == PackageManager.PERMISSION_GRANTED
        ) {
            changeServiceState(true)
        }
    }

    private fun sendCommand(command: String) {
        val intent = Intent(this, LocationService::class.java).apply {
            this.action = command
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

    }

    private val locationObserver: (Location) -> Unit = ::locationChanged

    private fun locationChanged(l: Location) {

        println("IMHERE")
        val tv = TextView(this@MainActivity)
        tv.textSize = 18F
        tv.text = "Latitude: ${l.latitude}; Longtitude: ${l.longitude}"
        println("Latitude: ${l.latitude}; Longtitude: ${l.longitude}")
        output.addView(tv)
    }

    fun updateDistance(d: Float){
        textViewDistance.text = LocationHelper.distance.toString()
    }


    private fun changeServiceState(forceStart: Boolean = false) {
        if(!LocationService.running || forceStart){
            println("Location service is running")
            sendCommand(Constants.START_LOCATION_SERVICE)
            LocationData.location.removeObservers(this)
        } else {
            println("Location service is not running")
            sendCommand(Constants.STOP_LOCATION_SERVICE)
            LocationData.location.removeObservers(this)
        }
    }
    override fun onStop(){
        super.onStop()
        LocationData.location.removeObserver(locationObserver)
    }

    override fun onStart(){
        super.onStart()
        if(LocationService.running)
            LocationData.location.observe(this, locationObserver)
    }
}