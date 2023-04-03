package com.example.stopwatch.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.stopwatch.MainActivity

object ServiceHelper {


    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(StopwatchService.STOPWATCH_STATE, StopwatchState.STARTED.name)
        }
        return PendingIntent.getActivity(
            context, StopwatchService.CLICK_RC, clickIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun stopPendingIntent(application: Context): PendingIntent {
        val stopIntent = Intent(application, StopwatchService::class.java).apply {
            putExtra(StopwatchService.STOPWATCH_STATE, StopwatchState.STOPPED.name)
        }
        return PendingIntent.getService(
            application, StopwatchService.STOP_RC, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun resumePendingIntent(application: Context): PendingIntent {
        val resumeIntent = Intent(application, StopwatchService::class.java).apply {
            putExtra(StopwatchService.STOPWATCH_STATE, StopwatchState.STARTED.name)
        }
        return PendingIntent.getService(
            application, StopwatchService.RESUME_RC, resumeIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelPendingIntent(application: Context): PendingIntent {
        val cancelIntent = Intent(application, StopwatchService::class.java).apply {
            putExtra(StopwatchService.STOPWATCH_STATE, StopwatchState.CANCELED.name)
        }
        return PendingIntent.getService(
            application, StopwatchService.CANCEL_RC, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, StopwatchService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }

}