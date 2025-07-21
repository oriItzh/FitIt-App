package com.example.archtectureproject.data.local
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.archtectureproject.data.model.Exercise

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(exercise: Exercise)

    @Update
    suspend fun updateItem(exercise: Exercise)

    @Delete
    suspend fun deleteItem(exercise: Exercise)

    @Query("SELECT * FROM exercises")
    fun getItems(): LiveData<List<Exercise>>

    @Query("DELETE from exercises")
    suspend fun deleteAll()
}