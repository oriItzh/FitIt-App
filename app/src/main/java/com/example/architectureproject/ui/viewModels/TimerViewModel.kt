package com.example.architectureproject.ui.viewModels

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.UUID

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    private val _timerValue = MutableLiveData<Long>()
    val timerValue: LiveData<Long> get() = _timerValue

    private var workId: UUID? = null
    private var isPaused = false
    private var pauseTime: Long = 0
    private var isActive = false

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "STOPWATCH_UPDATE" -> {
                    val timeElapsed = intent.getLongExtra("timeElapsed", 0L)
                    _timerValue.value = timeElapsed
                }
                "STOPWATCH_FINISHED" -> {
                    _statusMessage.value = "Stopwatch stopped"
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction("STOPWATCH_UPDATE")
            addAction("STOPWATCH_FINISHED")
        }
        LocalBroadcastManager.getInstance(application)
            .registerReceiver(timerUpdateReceiver, filter)
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication())
            .unregisterReceiver(timerUpdateReceiver)
    }

    fun setStatusMessage(message: String) {
        _statusMessage.value = message
    }

    fun setWorkId(id: UUID) {
        workId = id
    }

    fun getWorkId(): UUID? = workId

    fun pauseTimer() {
        if (!isPaused) {
            isPaused = true
            pauseTime = _timerValue.value ?: 0L
            WorkManager.getInstance(getApplication()).cancelWorkById(workId!!)
            setStatusMessage("Stopwatch paused")
        }
    }

    fun resumeTimer() {
        if (isPaused) {
            isPaused = false
            setStatusMessage("Stopwatch resumed")
            val inputData = Data.Builder()
                .putLong("initialTime", pauseTime)
                .build()

            val timerWorkRequest = OneTimeWorkRequestBuilder<TimerWorker>()
                .setInputData(inputData)
                .build()

            setWorkId(timerWorkRequest.id)
            WorkManager.getInstance(getApplication()).enqueue(timerWorkRequest)
        }
    }

    fun stopTimer() {
        if (isPaused){
            resumeTimer()
        }
        WorkManager.getInstance(getApplication()).cancelWorkById(workId!!)
        setStatusMessage("Stopwatch stopped")
        _timerValue.value = 0L
        workId = null
        isActive = false
    }

    fun startWorkout() {
        isActive = true
    }

    fun isActive(): Boolean = isActive

    fun isPaused(): Boolean = isPaused
}