package com.example.archtectureproject.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.archtectureproject.data.model.Workout

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(item: Workout)

    @Delete
    suspend fun deleteItem(vararg item: Workout)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItem(item: Workout)

    @Query("SELECT * from workouts_table ORDER BY title ASC")
    fun getItems() : LiveData<List<Workout>>

    @Query("SELECT * from workouts_table WHERE title LIKE :title")
    fun getItem(title:String) : Workout?

    @Query("DELETE from workouts_table")
    suspend fun deleteAll()
}