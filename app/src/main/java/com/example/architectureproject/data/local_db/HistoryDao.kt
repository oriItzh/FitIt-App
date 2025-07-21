package com.example.archtectureproject.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.archtectureproject.data.model.History
import com.example.archtectureproject.data.model.PersonalData
import java.util.Date

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Query("SELECT * FROM history_table WHERE date = :date")
    fun getHistoryByDate(date: String): LiveData<History?>

    @Query("SELECT * from history_table ORDER BY date ASC LIMIT 1")
    fun getFirstHistory() : LiveData<History>

    @Query("DELETE FROM history_table WHERE date = :date")
    suspend fun deleteHistoryByDate(date: String)

    @Query("SELECT * FROM history_table")
    suspend fun getAllHistoryList(): List<History>


}
