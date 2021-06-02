package com.example.tp_gps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.textViewDistance
import kotlinx.android.synthetic.main.activity_main.view.*
import org.w3c.dom.Text
import java.math.RoundingMode.valueOf

class MainActivity : AppCompatActivity() {

    private lateinit var startStopBtn: FloatingActionButton
    public var distance: Double = 0.0
    private lateinit var tvD: TextView
    private lateinit var output: LinearLayout
    public var dist:String = "0.0"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvD = findViewById<TextView>(R.id.textViewDistance)
    //    operationField.text="123"
        tvD.text="0.0"
        var i: Int = 0
        //operationField.text="543"
       // tvDistance = findViewById(R.id.textViewDistance)
        output = findViewById(R.id.output)
        startStopBtn = findViewById<FloatingActionButton>(R.id.startStopBtn).apply {
            setOnClickListener {
                println("Button touched")
                println(i.toString())
                println("distance   " + distance.toString())

               // textViewDistance.text = "100"
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
        textViewDistance.setOnClickListener{
            dist = renewDist()
            println("tvD Distance = $dist")
            textViewDistance.text = dist
        }

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
      //  println("IMHERE")
      //  val tv = TextView(this@MainActivity)
     //   tv.textSize = 18F
       // tv.text = "Latitude: ${l.latitude}; Longtitude: ${l.longitude}"
       // println("Latitude: ${l.latitude}; Longtitude: ${l.longitude}")
        updateDistance(distance.toString())
        //output.addView(tv)
    }


    fun updateDistance(d: String){
        //setContentView(R.layout.activity_main)
        dist = d
        distance = dist.toDouble()
        println("dist $dist distance $distance")
        setTextFields(d)
        //println("tvd text  = " + textViewDistance.text.toString())
        //val tvD = findViewById(R.id.textViewDistance) as TextView
       // println("tvD = $tvD")
    //    tvD.text = ""
       // textViewDistance = findViewById(R.id.textViewDistance)
       // textViewDistance.text = ""
       // var tvD = findViewById<TextView>(R.id.textViewDistance)
        //tvD.setText(d)
     //   textViewDistance.append(d)
     //   println("Update distance")
     //   tvDistance.text=""
        //textViewDistance.setText(d)
        //textViewDistance.text = ""
    //    tvD.text = ""
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
    private fun renewDist():String {

        println("distdist123 $dist dads$distance")
        return dist
    }

    private fun setTextFields(str: String)
    {
        println("str  $str distance_string  $dist    distance_double $distance")
    //    findViewById<TextView>(R.id.textViewDistance).text = ""
   // println("str = ${tvD.toString()}")
        /*val tv = TextView(this@MainActivity)
         tv.textSize = 18F
         tv.text = "Latitude:; Longtitude: "
        output.addView(tv)*/
    //tvD.text = str
      //  textViewDistance.text = ""
       // textViewDistance.append(str)
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