package com.example.runningtrackerapp.utils

import android.content.Context
import android.widget.TextView
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.db.entities.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView (
    val runs : List<Run>,
    c : Context,
    layoutId : Int) : MarkerView(c, layoutId) {

    private val date : TextView = findViewById(R.id.markerDate)
    private val time : TextView = findViewById(R.id.markerDuration)
    private val speed : TextView = findViewById(R.id.markerAverageSpeed)
    private val distance : TextView = findViewById(R.id.markerDistance)
    private val calories : TextView = findViewById(R.id.markerCalories)


    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)


        if(e == null) {
            return

        } else {

            val curRunId = e.x.toInt()
            val run = runs[curRunId]

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

            date.text = dateFormat.format(calendar.time)

            time.text = Utils.timerFormat(run.runningTimeInMillis, false)

            calories.text = "${ run.calBurned }Kcal"

            distance.text = "${ run.distanceInMeters / 1000f}km"

            speed.text = "${run.avgSpeedInKm}km/h"
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f, -height.toFloat())
    }


}