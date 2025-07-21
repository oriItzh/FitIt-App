package com.example.archtectureproject.data.repository

import android.app.Application
import com.example.architectureproject.data.local_db.ItemDatabase
import com.example.archtectureproject.data.local.WorkoutDao
import com.example.archtectureproject.data.model.Workout

class WorkoutsRepository(application: Application) {

    private var workoutDao: WorkoutDao?

    init {
        val db  = ItemDatabase.getDatabase(application)
        workoutDao = db.workoutDao()
    }

    fun getItems() = workoutDao?.getItems()

    suspend fun addItem(workout: Workout) {
        workoutDao?.addItem(workout)
    }

    suspend fun deleteItem(workout: Workout) {
        workoutDao?.deleteItem(workout)
    }

    suspend fun deleteAll() {
        workoutDao?.deleteAll()
    }

    suspend fun updateItem(workout: Workout) {
        workoutDao?.updateItem(workout)
    }

}