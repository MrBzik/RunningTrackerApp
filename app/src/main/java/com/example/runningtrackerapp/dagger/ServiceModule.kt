package com.example.runningtrackerapp.dagger



import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.ui.MainActivity
import com.example.runningtrackerapp.utils.Constants
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped



@Module
@InstallIn(ServiceComponent::class)

object ServiceModule {

    @ServiceScoped // means for lifetime of service will be only one instance
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app : Context)
                = LocationServices.getFusedLocationProviderClient(app)


    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext app : Context)
            = PendingIntent.getActivity(
        app, 0,
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.INTENT_TO_TRACKING_FRAG
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder (@ApplicationContext app : Context,
            pendingIntent: PendingIntent)
            = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running app")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)



}