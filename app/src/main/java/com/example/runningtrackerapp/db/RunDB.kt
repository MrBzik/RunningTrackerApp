package com.example.runningtrackerapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.runningtrackerapp.db.entities.Run

@Database(entities = [Run::class],
    version = 1
)

@TypeConverters(Converter::class)
abstract class RunDB : RoomDatabase() {

    // implemented by Room
   abstract fun getRunDao() : RunDAO

}