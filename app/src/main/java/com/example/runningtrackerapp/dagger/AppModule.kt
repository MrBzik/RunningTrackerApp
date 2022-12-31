package com.example.runningtrackerapp.dagger

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningtrackerapp.db.RunDAO
import com.example.runningtrackerapp.db.RunDB
import com.example.runningtrackerapp.repository.RunRepository
import com.example.runningtrackerapp.utils.Constants.DATABASE_NAME
import com.example.runningtrackerapp.utils.Constants.SHARED_PREF_NAME
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_IS_FIRST_RUN
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_USER_NAME
import com.example.runningtrackerapp.utils.Constants.SH_PREF_KEY_USER_WEIGHT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule  {

    @Provides
    @Singleton
    fun provideRunDatabase(
        @ApplicationContext app : Context)
            = Room.databaseBuilder(app, RunDB::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideRunDao(db : RunDB) = db.getRunDao()

    @Provides
    @Singleton
    fun provideSharedPref(
        @ApplicationContext app : Context) =
            app.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserName(sharePref : SharedPreferences)
        = sharePref.getString(SH_PREF_KEY_USER_NAME, "Bill") ?: "Fuck!!!!!"

    @Provides
    @Singleton
    fun provideWeight(sharePref : SharedPreferences)
            = sharePref.getFloat(SH_PREF_KEY_USER_WEIGHT, 66.6f)

    @Provides
    @Singleton
    fun provideIsFirstRun(sharePref : SharedPreferences)
            = sharePref.getBoolean(SH_PREF_KEY_IS_FIRST_RUN, true)



}