package com.example.architectureproject.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.architectureproject.ui.viewModels.LocationUpdatesLiveData

class LocationViewModel(application: Application)
    : AndroidViewModel(application) {

    val address : LiveData<String> = LocationUpdatesLiveData(application.applicationContext)

    private val _radiusGym = MutableLiveData<Double>()
    val radiusGym: LiveData<Double> get() = _radiusGym

    private val _radiusPark = MutableLiveData<Double>()
    val radiusPark: LiveData<Double> get() = _radiusPark

    fun setRadiusGym(item: Double) {
        _radiusGym.value = item
    }

    fun setRadiusPark(item: Double) {
        _radiusPark.value = item
    }
}