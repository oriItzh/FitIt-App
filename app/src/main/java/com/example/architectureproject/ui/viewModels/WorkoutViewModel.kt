package com.example.architectureproject.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.archtectureproject.data.model.Exercise
import com.example.archtectureproject.data.model.Workout
import com.example.archtectureproject.data.repository.WorkoutsRepository
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application)
    : AndroidViewModel(application) {


    private val repository = WorkoutsRepository(application)

    val items : LiveData<List<Workout>>? = repository.getItems()

    private val _chosenWorkout = MutableLiveData<Workout>()
    val chosenWorkout : LiveData<Workout> get() = _chosenWorkout

    fun setItem(item: Workout) {
        _chosenWorkout.value = item
    }

    fun addItem(item: Workout) {
        viewModelScope.launch {
            repository.addItem(item)
        }
    }

    fun deleteItem(item: Workout) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun deleteExerciseFromChosenWorkout(itemToDelete: Exercise) {
        val workoutToUpdate = _chosenWorkout.value ?: return // Exit if chosenWorkout is null
        val updatedExercises = workoutToUpdate.exercises.apply {
            remove(itemToDelete)
        }
        val updatedWorkout = workoutToUpdate.copy(exercises = updatedExercises)
        viewModelScope.launch {
            repository.updateItem(updatedWorkout)
        }
        _chosenWorkout.value = updatedWorkout // Update the chosen workout directly
    }

    fun setActiveWorkout(item: Workout){
        _chosenWorkout.value = item
    }

}