package com.example.archtectureproject.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.archtectureproject.data.model.PersonalData

@Dao
interface PersonalDataDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun addItem(item: PersonalData)

        @Delete
        suspend fun deleteItem(vararg item: PersonalData)

        @Update(onConflict = OnConflictStrategy.REPLACE)
        fun updateItem(item: PersonalData)

        @Query("SELECT * from personalData_table ORDER BY title ASC LIMIT 1")
        fun getOnlyItem() : LiveData<PersonalData>

        @Query("SELECT * from personalData_table WHERE title LIKE :title")
        fun getItem(title:String) : PersonalData

        @Query("DELETE from personalData_table")
        suspend fun deleteAll()
    }
