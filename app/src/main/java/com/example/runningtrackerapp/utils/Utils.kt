package com.example.runningtrackerapp.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Build.VERSION_CODES.Q
import com.example.runningtrackerapp.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit


object Utils {


    fun calculateSingleDistance(polyline: Polyline) : Float {

        val pos1 = polyline.last()
        val pos2 = polyline[polyline.lastIndex -1]

        val floatArray = FloatArray(1)

        Location.distanceBetween(
            pos1.latitude, pos1.longitude,
            pos2.latitude, pos2.longitude,
            floatArray
        )
        return floatArray[0]
    }

//    fun calculateTotalDistance(polyline : Polyline) : Float {
//
//        var resultDistance = 0f
//
//        for(i in 0..polyline.size-2) {
//
//            val pos1 = polyline[i]
//            val pos2 = polyline[i+1]
//
//            val floatArray = FloatArray(1)
//
//            Location.distanceBetween(
//                pos1.latitude, pos1.longitude,
//                pos2.latitude, pos2.longitude,
//                floatArray
//            )
//            resultDistance += floatArray[0]
//        }
//
//        return resultDistance
//    }

    fun hasLocationPermissions(context: Context) =

        if(Build.VERSION.SDK_INT > Q){
            EasyPermissions.hasPermissions(context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
          else  EasyPermissions.hasPermissions(context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )


        fun timerFormat(mills : Long, isMillsRequired : Boolean) : String {

            var milliseconds = mills

            val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
            milliseconds -= TimeUnit.HOURS.toMillis(hours)

            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
            milliseconds -= TimeUnit.SECONDS.toMillis(seconds)

            return if(!isMillsRequired){

                String.format("%02d:%02d:%02d", hours, minutes, seconds)

            } else {

                String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds /10)

            }
        }


}