package com.example.runningtrackerapp.utils

import android.graphics.Color

object Constants {


   const val DATABASE_NAME = "running_db"

   const val COMMAND_START_SERVICE = "START_SERVICE"

   const val COMMAND_PAUSE_SERVICE = "PAUSE_SERVICE"

   const val COMMAND_STOP_SERVICE = "STOP_SERVICE"

   const val INTENT_TO_TRACKING_FRAG = "INTENT_TO_TRACKING_FRAG"

   const val NOTIFICATION_CHANEL_ID = "Tracking_chanel"

   const val NOTIFICATION_CHANEL_NAME = "Tracking"

   const val NOTIFICATION_ID = 1

   const val LOCATION_UPDATE_INTERVAL = 5000L

   const val LOCATION_UPDATE_MIN_INTERVAL = 4000L

   const val POLYLINE_COLOR = Color.MAGENTA

   const val POLYLINE_WIDTH = 8f

   const val MAP_ZOOM = 15f

   const val TIMER_UPDATE_INTERVAL = 100L

   const val SHARED_PREF_NAME = "sharedPreferences"

   const val SH_PREF_KEY_USER_NAME = "userName"

   const val SH_PREF_KEY_USER_WEIGHT = "userWeight"

   const val SH_PREF_KEY_IS_FIRST_RUN = "isFirstRun"

}