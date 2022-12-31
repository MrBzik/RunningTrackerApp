package com.example.runningtrackerapp.db.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "run_table")
data class Run (
    var img : Bitmap? = null,

    // date of run, converted into milliseconds
    var timestamp : Long = 0L,

    var runningTimeInMillis : Long = 0L,

    var distanceInMeters : Int = 0,

    var avgSpeedInKm : Float = 0f,

    var calBurned : Int = 0

        )
{

    @PrimaryKey(autoGenerate = true)
    var id : Int? = null

}