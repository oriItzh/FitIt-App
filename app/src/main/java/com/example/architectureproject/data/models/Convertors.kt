package com.example.architectureproject.data.models

import androidx.room.TypeConverter
import com.example.archtectureproject.data.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convertors {

//        @TypeConverter
//        fun fromExerciseList(exercises: List<Item?>?): String {
//            val gson = Gson()
//            val type = object : TypeToken<List<Item?>?>() {}.type
//            return gson.toJson(exercises, type)
//        }
//
//        @TypeConverter
//        fun toExerciseList(exerciseString: String): List<Item?>? {
//            val gson = Gson()
//            val type = object : TypeToken<List<Item?>?>() {}.type
//            return gson.fromJson(exerciseString, type)
//        }

    @TypeConverter
    fun fromString(value: String): List<Exercise> {
        val listType = object : TypeToken<List<Exercise>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Exercise>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

}