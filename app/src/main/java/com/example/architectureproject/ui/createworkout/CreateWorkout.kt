package com.example.architectureproject.ui.createworkout

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.architectureproject.R
import com.example.architectureproject.databinding.CreateWorkoutFragmentBinding
import com.example.architectureproject.ui.viewModels.ExerciseViewModel


import com.example.archtectureproject.data.model.Workout

import com.example.architectureproject.ui.viewModels.WorkoutViewModel


class CreateWorkout : Fragment() {

    private var _binding: CreateWorkoutFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModelExercise : ExerciseViewModel by activityViewModels()
    private val viewModelWorkout : WorkoutViewModel by activityViewModels()

    private lateinit var adapter: ExercisesListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CreateWorkoutFragmentBinding.inflate(inflater,container,false)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If you choose exercises and you did not set a title, color it back to white
        binding.nameWorkoutEt.setOnFocusChangeListener {_, clicked ->
            if (clicked) {
                binding.nameWorkoutLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.main_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_delete -> {
                        viewModelExercise.deleteAll()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Initialize the adapter
        val numberOfColumns =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1
        binding.recycler.layoutManager = GridLayoutManager(requireContext(),numberOfColumns)
        adapter = ExercisesListAdapter(emptyList(), object : ExercisesListAdapter.ItemListener {
            override fun onItemClicked(index: Int) {
            }
        })

        binding.recycler.adapter = adapter

        // Observe the items from the ViewModel
        viewModelExercise.allExercises?.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        binding.doneButton.setOnClickListener {
            val workoutName = binding.nameWorkoutEt.text.toString()
            val chosenExercises = adapter.getChosenItems()

            if (workoutName.isEmpty() && chosenExercises.isNotEmpty()) {
                binding.nameWorkoutLayout.defaultHintTextColor =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else if (workoutName.isNotEmpty() && chosenExercises.isNotEmpty()) {
                // Create and add the workout to the WorkoutViewModel
                val workout = Workout(workoutName, chosenExercises.toMutableList())
                viewModelWorkout.addItem(workout)
                findNavController().navigate(R.id.action_createWorkout_to_allWorkoutsFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}