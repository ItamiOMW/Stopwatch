package com.example.stopwatch.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.stopwatch.R
import com.example.stopwatch.utils.formatTime
import com.example.stopwatch.utils.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StopwatchService : Service() {


    companion object {

        const val NOTIFICATION_CHANNEL_ID = "stopwatch_notification_id"
        const val NOTIFICATION_CHANNEL_NAME = "stopwatch_notification"
        const val NOTIFICATION_ID = 123

        const val ACTION_START_SERVICE = "action_start_service"
        const val ACTION_STOP_SERVICE = "action_stop_service"
        const val ACTION_CANCEL_SERVICE = "action_cancel_service"

        const val STOPWATCH_STATE = "stopwatch_state"

        //RQ == Request Code
        const val CLICK_RC = 1
        const val CANCEL_RC = 2
        const val STOP_RC = 3
        const val RESUME_RC = 4
    }

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder


    private val binder = StopwatchBinder()

    private var duration = Duration.ZERO
    private lateinit var timer: Timer

    var seconds by mutableStateOf("00")
        private set

    var minutes by mutableStateOf("00")
        private set

    var hours by mutableStateOf("00")
        private set

    var state by mutableStateOf(StopwatchState.IDLE)
        private set


    override fun onBind(intent: Intent?): IBinder = binder


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(STOPWATCH_STATE)) {
            StopwatchState.STARTED.name -> {
                setStopButton()
                startForegroundService()
                startStopwatch { h, m, s ->
                    updateNotification(h, m, s)
                }
            }
            StopwatchState.STOPPED.name -> {
                stopStopwatch()
                setResumeButton()
            }
            StopwatchState.CANCELED.name -> {
                stopStopwatch()
                cancelStopwatch()
                stopForegroundService()
            }
        }
        intent?.action?.let { action ->
            when (action) {
                ACTION_START_SERVICE -> {
                    setStopButton()
                    startForegroundService()
                    startStopwatch { h, m, s ->
                        updateNotification(h, m, s)
                    }
                }
                ACTION_STOP_SERVICE -> {
                    stopStopwatch()
                    setResumeButton()
                }
                ACTION_CANCEL_SERVICE -> {
                    stopStopwatch()
                    cancelStopwatch()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startStopwatch(onTick: (h: String, m: String, s: String) -> Unit) {
        state = StopwatchState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours, minutes, seconds)
        }
    }

    private fun stopStopwatch() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        state = StopwatchState.STOPPED
    }

    private fun cancelStopwatch() {
        duration = Duration.ZERO
        state = StopwatchState.IDLE
        updateTimeUnits()
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StopwatchService.hours = hours.toInt().pad()
            this@StopwatchService.minutes = minutes.pad()
            this@StopwatchService.seconds = seconds.pad()
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTime(seconds, minutes, hours)
            ).build()
        )
    }

    private fun setStopButton() {
        notificationBuilder.clearActions()
        notificationBuilder.addAction(
            NotificationCompat.Action(
                0,
                getString(R.string.action_stop),
                ServiceHelper.stopPendingIntent(application)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun setResumeButton() {
        notificationBuilder.clearActions()
        notificationBuilder.addAction(
            NotificationCompat.Action(
                0,
                getString(R.string.action_resume),
                ServiceHelper.resumePendingIntent(application)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): StopwatchService = this@StopwatchService
    }

}