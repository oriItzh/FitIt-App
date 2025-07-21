package com.example.archtectureproject.data.model

import android.text.style.ReplacementSpan
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "exercises")
data class Exercise(

    @ColumnInfo(name = "title")
    val name:String,

    @ColumnInfo(name = "muscleGroup")
    val muscleGroup:String,

    @ColumnInfo(name = "weight")
    var weight:String?,

    @ColumnInfo(name = "sets")
    val sets:Int?,

    @ColumnInfo(name = "reps")
    val reps:Int?,

    @ColumnInfo(name = "icon")
    val iconUri:Int,

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
) {
    /**
     * Estimate the exercise time based on the number of sets and reps.
     * Assuming an average of 3 seconds per rep and 30 seconds rest between sets.
     */
    fun estimatedTime(): Int {
        val averageTimePerRep = 2 // average time per rep in seconds
        val restTimePerSet = 60 // rest time between sets in seconds

        // Calculate total time for reps
        val totalRepsTime = (reps ?: 0) * (sets ?: 0) * averageTimePerRep

        // Calculate total rest time
        val totalRestTime = (sets ?: 0 - 1) * restTimePerSet

        // Total estimated time
        return totalRepsTime + totalRestTime
    }
}