package com.example.architectureproject.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.archtectureproject.data.model.History
import com.example.archtectureproject.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application)
    : AndroidViewModel(application) {

    private val historyRepository = HistoryRepository(application)

    private val _chosenHistory = MutableLiveData<History>()
    val chosenHistory: LiveData<History> get() = _chosenHistory

    private val _allHistory = MutableLiveData<List<History>>()
    val allHistory: LiveData<List<History>> get() = _allHistory

    init {
        fetchAllHistory()
    }

    fun addHistory(history: History) {
        viewModelScope.launch {
            historyRepository.insert(history)
        }
        // _chosenHistory.value = historyRepository.getHistoryByDate(history.date)
    }

    // updates the historyData LiveData
    fun fetchHistoryByDate(date: String) : LiveData<History?>? {
        return historyRepository.getHistoryByDate(date)
    }

    fun deleteHistoryByDate(date: String){
        viewModelScope.launch {
            historyRepository.deleteHistoryByDate(date)
            fetchAllHistory()
        }
    }

    fun setChosenHistory(item: History) {
        _chosenHistory.value = item
    }

    fun getFirstHistory() : LiveData<History>? {
        return historyRepository.getFirstHistory()
    }

    fun fetchAllHistory() {
        viewModelScope.launch {
            _allHistory.value = historyRepository.getAllHistory()
        }
    }
}