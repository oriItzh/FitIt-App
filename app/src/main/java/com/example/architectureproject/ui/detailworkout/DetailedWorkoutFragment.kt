package com.example.architectureproject.ui.detailworkout

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.architectureproject.R
import com.example.architectureproject.databinding.DetailWorkoutLayoutBinding
import com.example.architectureproject.utils.autoCleared
import com.example.architectureproject.ui.viewModels.WorkoutViewModel

class DetailedWorkoutFragment : Fragment(){

    private  var binding: DetailWorkoutLayoutBinding by autoCleared()
    private val viewModelWorkout : WorkoutViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailWorkoutLayoutBinding.inflate(layoutInflater,container,false)

        binding.goBack.setOnClickListener{
            findNavController().navigate(R.id.action_detailedWorkoutFragment_to_allWorkoutsFragment)
        }

        binding.addExerciseButton.setOnClickListener{
            findNavController().navigate(R.id.action_detailedWorkoutFragment_to_updateWorkout)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelWorkout.chosenWorkout.observe(viewLifecycleOwner) {
            binding.workoutTitle.text = it.title
            // Adds the exercises of the selected workout
            val exercises = it.exercises
            binding.recycler.adapter = SelectedWorkoutAdapter(exercises, object : SelectedWorkoutAdapter.ItemListener {
                override fun onItemClicked(index: Int) {
                }

            })
            val numberOfColumns =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1
            binding.recycler.layoutManager = GridLayoutManager(requireContext(),numberOfColumns)

        }

        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            )= makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val confirmDialogBuilder = AlertDialog.Builder(requireContext())
                confirmDialogBuilder.setMessage(getString(R.string.delete_message_exercise))
                confirmDialogBuilder.setCancelable(false)

                confirmDialogBuilder.setPositiveButton(getString(R.string.yes)) {dialog,_ ->
                    Toast.makeText(requireContext(), getString(R.string.exercise_deleted), Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    // Delete an exercise
                    val adapter = binding.recycler.adapter as? SelectedWorkoutAdapter
                    adapter?.let {
                        val itemToDelete = it.itemAt(viewHolder.adapterPosition)
                        it.removeItem(viewHolder.adapterPosition)
                        viewModelWorkout.deleteExerciseFromChosenWorkout(itemToDelete)

                    }
                }

                confirmDialogBuilder.setNeutralButton( getString(R.string.no)) {dialog,_ ->
                    binding.recycler.adapter!!.notifyItemChanged(viewHolder.adapterPosition)
                    dialog.dismiss()
                }
                confirmDialogBuilder.create().show()

            }
        }).attachToRecyclerView(binding.recycler)

    }

}