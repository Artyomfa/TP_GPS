package com.example.tp_gps

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.provider.SyncStateContract
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService

class LocationService: LifecycleService() {
    companion object{
        var running = false
    }

    private fun locationChanged(l: Location){
        LocationData.location.postValue(l)
    }
    override fun onCreate(){
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also{
            when(it.action){
                Constants.START_LOCATION_SERVICE -> {
                    running = true
                    startForeground(intent)
                    LocationHelper.startLocationListening(this.applicationContext, ::locationChanged)
                }
                Constants.STOP_LOCATION_SERVICE -> {
                    running = false
                    LocationHelper.stopLocating()
                    stopService(intent)
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    fun startForeground(service: Intent?) {
        val channelId=
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }else {
            ""
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        startForeground(Constants.FOREGROUND_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelName = "Location Service"
        val chan = NotificationChannel(Constants.CHANNEL_ID,
            channelName, NotificationManager.IMPORTANCE_LOW)
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return Constants.CHANNEL_ID
    }
}