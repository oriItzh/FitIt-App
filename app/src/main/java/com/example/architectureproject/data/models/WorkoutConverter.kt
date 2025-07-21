// Place this file in the package com.example.architectureproject.data.models
package com.example.architectureproject.data.models

import androidx.room.TypeConverter
import com.example.archtectureproject.data.model.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WorkoutConverter {

    @TypeConverter
    fun fromWorkout(workout: Workout?): String? {
        if (workout == null) return null
        val type = object : TypeToken<Workout>() {}.type
        return Gson().toJson(workout, type)
    }

    @TypeConverter
    fun toWorkout(workoutString: String?): Workout? {
        if (workoutString == null) return null
        val type = object : TypeToken<Workout>() {}.type
        return Gson().fromJson(workoutString, type)
    }
}
