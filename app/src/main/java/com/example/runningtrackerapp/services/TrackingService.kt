package com.example.runningtrackerapp.services


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.utils.Constants.COMMAND_PAUSE_SERVICE
import com.example.runningtrackerapp.utils.Constants.COMMAND_START_SERVICE
import com.example.runningtrackerapp.utils.Constants.COMMAND_STOP_SERVICE
import com.example.runningtrackerapp.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningtrackerapp.utils.Constants.LOCATION_UPDATE_MIN_INTERVAL
import com.example.runningtrackerapp.utils.Constants.NOTIFICATION_CHANEL_ID
import com.example.runningtrackerapp.utils.Constants.NOTIFICATION_CHANEL_NAME
import com.example.runningtrackerapp.utils.Constants.NOTIFICATION_ID
import com.example.runningtrackerapp.utils.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningtrackerapp.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.round


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>


@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isServiceKilled = false

    private var isFirstRun = true

    @Inject
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var updateNotification : NotificationCompat.Builder

    lateinit var notificationManager : NotificationManager

    private val runTimeInSeconds = MutableLiveData<Long>()

    private var totalDistance = 0f



    companion object {

        val currentSpeed = MutableLiveData<Float>()

        val distanceSoFar = MutableLiveData<Float>()

        val runTimeInMillis = MutableLiveData<Long>()

        val isTracking = MutableLiveData<Boolean>()

        val pathPoints = MutableLiveData<Polylines>()

    }

   private fun setInitialState(){

       isTracking.postValue(false)
       pathPoints.postValue(mutableListOf())
       runTimeInSeconds.postValue(0L)
       runTimeInMillis.postValue(0L)

    }

   private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private fun addNewCoordinates(location : Location) {

            val latlng = LatLng(location.latitude, location.longitude)

            pathPoints.value?.apply{

                last().add(latlng)

                pathPoints.postValue(this)


        }
    }

    private fun calculateDistance(){

        pathPoints.value?.let {
            if(it.last().size >1) {

                val distance = Utils.calculateSingleDistance(it.last())

                totalDistance += distance

                distanceSoFar.postValue(totalDistance)

            }
        }


    }

   private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            if(isTracking.value!!) {
                result.locations.forEach { location ->
                   addNewCoordinates(location)
                }
            }

            val location = result.lastLocation

            location?.speed?.let {

                val speed = round(it * 3.6f*10f)/10f

                currentSpeed.postValue(speed)

            }

            calculateDistance()

        }
    }

    @SuppressLint("MissingPermission")
    fun updateTrackingStatus(isTracking : Boolean){

        if(isTracking) {
            if(Utils.hasLocationPermissions(this)) {

                val locationRequest = LocationRequest.Builder(
                    /* priority = */ Priority.PRIORITY_HIGH_ACCURACY,
                    /* intervalMillis = */ LOCATION_UPDATE_INTERVAL
                )
                    .setMinUpdateIntervalMillis(LOCATION_UPDATE_MIN_INTERVAL)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
        else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun pauseService(){

        isTracking.postValue(false)
        isTimerRunning = false

    }

    private fun killService(){

        isServiceKilled = true
        isFirstRun = true
        pauseService()
        setInitialState()
        if(Build.VERSION.SDK_INT>=24) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }

        stopSelf()
    }


    private var isTimerRunning = false
    private var startingTime = 0L
    private var totalTime = 0L
    private var lapTime = 0L
    private var toSecondsTime = 0L


    private fun startTimer(){

        addEmptyPolyline()
        isTracking.postValue(true)
        isTimerRunning = true
        startingTime = System.currentTimeMillis()


        CoroutineScope(Dispatchers.Main).launch {

            while(isTracking.value!!){

                lapTime = System.currentTimeMillis() - startingTime

                runTimeInMillis.postValue(totalTime + lapTime)

                if(runTimeInMillis.value!! >= toSecondsTime + 1000L) {
                    runTimeInSeconds.postValue(runTimeInSeconds.value!! + 1)
                    toSecondsTime += 1000L


                }

                delay(TIMER_UPDATE_INTERVAL)
            }

            totalTime += lapTime
        }


    }


    private fun updateNotificationState(isTracking: Boolean){

        val notificationActionText = if(isTracking) "Pause" else "Resume"

        val pendingIntent = PendingIntent.getService(
            this, 1,
            Intent(this, TrackingService::class.java).also {

                if(isTracking) {
                    it.action = COMMAND_PAUSE_SERVICE
                } else {

                    it.action = COMMAND_START_SERVICE
                }
            }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        updateNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(updateNotification, ArrayList<NotificationCompat.Action>())
        }


        if(!isServiceKilled) {

            updateNotification = baseNotificationBuilder

            updateNotification
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)

            notificationManager.notify(NOTIFICATION_ID, updateNotification.build())

        }
    }


    override fun onCreate() {
        super.onCreate()

        setInitialState()

        updateNotification = baseNotificationBuilder

       notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        isTracking.observe(this) {
            updateTrackingStatus(it)
            updateNotificationState(it)

        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            when (it.action) {

                COMMAND_START_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service ...")
                        startTimer()
                    }

                }
                COMMAND_PAUSE_SERVICE -> pauseService()

                COMMAND_STOP_SERVICE ->  killService()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){

        startTimer()

        isTracking.postValue(true)



        if(Build.VERSION.SDK_INT >= O) {
                createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())


            runTimeInSeconds.observe(this){

                if(!isServiceKilled){

                val notification = updateNotification
                notification.setContentText(Utils.timerFormat(it * 1000L, false))

                notificationManager.notify(NOTIFICATION_ID, notification.build())

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val notificationChanel = NotificationChannel(NOTIFICATION_CHANEL_ID,
            NOTIFICATION_CHANEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(notificationChanel)
    }

}