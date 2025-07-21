package com.example.archtectureproject.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personalData_table")
data class PersonalData(

    @ColumnInfo(name = "title")
    val firstName:String,

    @ColumnInfo(name = "surname")
    val surname:String,

    @ColumnInfo(name = "age")
    val age:String,

    @ColumnInfo(name = "height")
    val height:String,

    @ColumnInfo(name = "weight")
    val weight:String,

    @ColumnInfo(name = "gender")
    val gender:String,

    @ColumnInfo(name = "photo")
    val photo: String?)
{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}

