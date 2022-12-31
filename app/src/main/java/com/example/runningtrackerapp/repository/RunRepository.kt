package com.example.runningtrackerapp.repository

import androidx.lifecycle.LiveData
import com.example.runningtrackerapp.db.RunDAO
import com.example.runningtrackerapp.db.entities.Run

import javax.inject.Inject


class RunRepository @Inject constructor (private val runDao: RunDAO) {


    suspend fun insertRun(run: Run) {
        runDao.insertRun(run)
    }

    suspend fun deleteRun(run: Run) {
        runDao.deleteRun(run)
    }

    fun getAllRunsByDate() : LiveData<List<Run>> {
        return runDao.getAllRunsByDate()
    }

    fun getAllRunsByDistance() : LiveData<List<Run>> {
        return runDao.getAllRunsByDistance()
    }

    fun getAllRunsByTime() : LiveData<List<Run>> {
        return runDao.getAllRunsByTime()
    }

    fun getAllRunsBySpeed() : LiveData<List<Run>> {
        return runDao.getAllRunsBySpeed()
    }

    fun getAllRunsByCal() : LiveData<List<Run>> {
        return runDao.getAllRunsByCal()
    }

    fun getTotalTime() : LiveData<Long> {
        return runDao.getTotalTime()
    }

    fun getTotalCal() : LiveData<Int> {
        return runDao.getTotalCal()
    }

    fun getTotalDistance() : LiveData<Int> {
        return runDao.getTotalDistance()
    }

    fun getTotalAvgSpeed() : LiveData<Float> {
        return runDao.getTotalAvgSpeed()
    }

}