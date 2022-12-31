package com.example.runningtrackerapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.runningtrackerapp.db.entities.Run

@Dao
interface RunDAO {

        @Insert(onConflict = OnConflictStrategy.REPLACE )
        suspend fun insertRun(run: Run)

        @Delete
        suspend fun deleteRun(run: Run)

        @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
        fun getAllRunsByDate() : LiveData<List<Run>>

        @Query("SELECT * FROM run_table ORDER BY distanceInMeters DESC")
        fun getAllRunsByDistance() : LiveData<List<Run>>

        @Query("SELECT * FROM run_table ORDER BY runningTimeInMillis DESC")
        fun getAllRunsByTime() : LiveData<List<Run>>

        @Query("SELECT * FROM run_table ORDER BY avgSpeedInKm DESC")
        fun getAllRunsBySpeed() : LiveData<List<Run>>

        @Query("SELECT * FROM run_table ORDER BY calBurned DESC")
        fun getAllRunsByCal() : LiveData<List<Run>>

        @Query("SELECT SUM(runningTimeInMillis) FROM run_table")
        fun getTotalTime() : LiveData<Long>

    @Query("SELECT SUM(calBurned) FROM run_table")
    fun getTotalCal() : LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM run_table")
    fun getTotalDistance() : LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKm) FROM run_table")
    fun getTotalAvgSpeed() : LiveData<Float>


}