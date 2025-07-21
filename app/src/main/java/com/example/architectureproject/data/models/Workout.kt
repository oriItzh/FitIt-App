package com.example.archtectureproject.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.architectureproject.data.models.Convertors

@Entity(tableName = "workouts_table")
data class Workout(

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "exercises")
    @TypeConverters(Convertors::class)
    val exercises: MutableList<Exercise>
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        mutableListOf<Exercise>().apply {
            parcel.readList(this, Exercise::class.java.classLoader)
        }
    ) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeList(exercises)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout = Workout(parcel)
        override fun newArray(size: Int): Array<Workout?> = arrayOfNulls(size)
    }
    /**
     * Calculate the total estimated time for the workout based on the exercises time.
     * Including rest time between exercises.
     */
    fun totalEstimatedTime(): List<Int> {
        val restTimeBetweenExercises = 90 // rest time between exercises in seconds
        val totalExerciseTime = exercises.sumOf { it.estimatedTime() }
        val totalRestTime = if (exercises.size > 1) (exercises.size - 1) * restTimeBetweenExercises else 0
        val totalTimeInSeconds = totalExerciseTime + totalRestTime
        // Convert total time in seconds to HH:MM:SS format

        val hours = totalTimeInSeconds / 3600
        val minutes = (totalTimeInSeconds % 3600) / 60
        val seconds = totalTimeInSeconds % 3600 % 60
        return listOf(hours,minutes,seconds)
    }
}