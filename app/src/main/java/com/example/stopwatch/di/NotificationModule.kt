package com.example.stopwatch.di

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.stopwatch.R
import com.example.stopwatch.service.ServiceHelper
import com.example.stopwatch.service.StopwatchService
import com.example.stopwatch.utils.STOPWATCH_INITIAL_TEXT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(
        application: Application,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(application, StopwatchService.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(application.getString(R.string.app_name))
            .setContentText(STOPWATCH_INITIAL_TEXT)
            .setSmallIcon(R.drawable.stopwatch)
            .setOngoing(true)
            .addAction(
                0,
                application.getString(R.string.action_stop),
                ServiceHelper.stopPendingIntent(application)
            )
            .addAction(
                0,
                application.getString(R.string.action_cancel),
                ServiceHelper.cancelPendingIntent(application)
            )
            .setContentIntent(ServiceHelper.clickPendingIntent(application))
    }


    @Provides
    @ServiceScoped
    fun provideNotificationManager(
        application: Application
    ): NotificationManager {
        return application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}