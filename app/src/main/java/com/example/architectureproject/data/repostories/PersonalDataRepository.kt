package com.example.archtectureproject.data.repository

import android.app.Application
import com.example.architectureproject.data.local_db.ItemDatabase
import com.example.archtectureproject.data.local.PersonalDataDao
import com.example.archtectureproject.data.model.PersonalData

class PersonalDataRepository(application: Application) {

        private var personalDataDao: PersonalDataDao?

        init {
            val db  = ItemDatabase.getDatabase(application)
            personalDataDao = db.personalDataDao()
        }

        fun getOnlyItem() = personalDataDao?.getOnlyItem()

        suspend fun addItem(item: PersonalData) {
            personalDataDao?.addItem(item)
        }

        suspend fun deleteItem(item: PersonalData) {
            personalDataDao?.deleteItem(item)
        }

        suspend fun deleteAll() {
            personalDataDao?.deleteAll()
        }

}