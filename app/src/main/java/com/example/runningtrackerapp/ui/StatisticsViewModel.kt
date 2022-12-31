package com.example.runningtrackerapp.ui

import androidx.lifecycle.ViewModel
import com.example.runningtrackerapp.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor (
    val repository: RunRepository
    ) : ViewModel() {


   val totalTime = repository.getTotalTime()

   val totalDistance = repository.getTotalDistance()

   val totalCalories = repository.getTotalCal()

   val totalAvgSpeed = repository.getTotalAvgSpeed()

    val runsSortedByDate = repository.getAllRunsByDate()


}