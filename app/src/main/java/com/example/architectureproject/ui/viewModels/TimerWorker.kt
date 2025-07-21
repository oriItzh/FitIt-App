package com.example.architectureproject.ui.viewModels

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class TimerWorker (appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun doWork(): Result {
        val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        val initialTime = inputData.getLong("initialTime", 0L)
        var timeElapsed = initialTime
        val startTime = System.currentTimeMillis() - initialTime

        // Acquire WakeLock
        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StopwatchWorker::WakelockTag")
        wakeLock?.acquire()

        try {
            while (!isStopped) {
                Thread.sleep(10) // Update every 10 milliseconds
                timeElapsed = System.currentTimeMillis() - startTime

                val intent = Intent("STOPWATCH_UPDATE")
                intent.putExtra("timeElapsed", timeElapsed)
                broadcastManager.sendBroadcast(intent)
            }
        } finally {
            // Release WakeLock
            wakeLock?.release()
        }

        val intent = Intent("STOPWATCH_FINISHED")
        broadcastManager.sendBroadcast(intent)

        return Result.success()
    }
}