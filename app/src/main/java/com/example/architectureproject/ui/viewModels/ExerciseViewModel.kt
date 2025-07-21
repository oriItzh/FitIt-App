package com.example.architectureproject.ui.viewModels

import ExerciseRepository
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.archtectureproject.data.model.Exercise
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository = ExerciseRepository(application)

    val allExercises: LiveData<List<Exercise>>? = repository.getAllExercises()
    private val _chosenItem = MutableLiveData<Exercise>()
    val chosenItem : LiveData<Exercise> get() = _chosenItem
    private val _exerciseUpdated = MutableLiveData<Boolean>()
    val exerciseUpdated: LiveData<Boolean> get() = _exerciseUpdated

    var isChanged = false

    fun setItem(item: Exercise) {
        _chosenItem.value = item
    }

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.insert(exercise)
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.update(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.delete(exercise)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
