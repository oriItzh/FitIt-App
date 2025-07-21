package com.example.architectureproject.ui.allworkouts

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.architectureproject.R
import com.example.architectureproject.databinding.AllWorkoutsFragmentBinding
import com.example.architectureproject.ui.viewModels.TimerViewModel
import com.example.architectureproject.ui.viewModels.TimerWorker
import com.example.architectureproject.ui.viewModels.WorkoutViewModel

class AllWorkoutsFragment : Fragment() {

        private var _binding: AllWorkoutsFragmentBinding? = null
        private val binding get() = _binding!!

        private val viewModelWorkout : WorkoutViewModel by activityViewModels()
        private val viewModelTimer: TimerViewModel by activityViewModels()

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = AllWorkoutsFragmentBinding.inflate(inflater,container,false)

            arguments?.getString("title")?.let {
                Toast.makeText(requireActivity(),it, Toast.LENGTH_SHORT).show()
            }
            binding.createButton.setOnClickListener {

                findNavController().navigate(R.id.action_allWorkoutsFragment_to_createWorkout)

            }

            binding.goBackButton.setOnClickListener {

                findNavController().navigate(R.id.action_allWorkoutsFragment_to_profileFragment)

            }

            return binding.root
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

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
                            viewModelWorkout.deleteAll()
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

            viewModelWorkout.items?.observe(viewLifecycleOwner) {

                binding.recycler.adapter = WorkoutAdapter(it, object : WorkoutAdapter.ItemListener {
                    override fun onItemClicked(index: Int) {
                        viewModelWorkout.setItem(it[index])
                        findNavController().navigate(R.id.action_allWorkoutsFragment_to_detailedWorkoutFragment)
                    }

                    override fun onItemLongClick(index: Int) {
                        viewModelWorkout.setActiveWorkout(it[index])
                        showStartWorkoutDialog()
                    }
                })
            }
            val numberOfColumns =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2 else 1
            binding.recycler.layoutManager = GridLayoutManager(requireContext(),numberOfColumns)


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
                    confirmDialogBuilder.setMessage(getString(R.string.delete_message_workout))
                    confirmDialogBuilder.setCancelable(false)

                    confirmDialogBuilder.setPositiveButton( getString(R.string.yes)) {dialog,_ ->
                        dialog.dismiss()
                        viewModelWorkout.deleteItem((binding.recycler.adapter as WorkoutAdapter)
                            .itemAt(viewHolder.adapterPosition))
                        binding.recycler.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                    }

                    confirmDialogBuilder.setNeutralButton( getString(R.string.no)) {dialog,_ ->
                        binding.recycler.adapter!!.notifyItemChanged(viewHolder.adapterPosition)
                        dialog.dismiss()
                    }
                    confirmDialogBuilder.create().show()
                }
            }).attachToRecyclerView(binding.recycler)
        }

    private fun showStartWorkoutDialog() { // presenting a popup dialog and lets the customer start a timed workout
        val confirmDialogBuilder = AlertDialog.Builder(requireContext())
        confirmDialogBuilder.setCancelable(false)
        confirmDialogBuilder.setPositiveButton( getString(R.string.start_workout)) {dialog,_ ->
            if (viewModelTimer.isActive()) {
                Toast.makeText(requireContext(), getString(R.string.Exist_active_workout), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.workout_started), Toast.LENGTH_LONG).show()

                viewModelTimer.setStatusMessage(getString(R.string.activity_started))
                viewModelTimer.startWorkout()

                val timerWorkRequest = OneTimeWorkRequestBuilder<TimerWorker>().build()

                viewModelTimer.setWorkId(timerWorkRequest.id)
                WorkManager.getInstance(requireContext()).enqueue(timerWorkRequest)
            }

            dialog.dismiss()
            findNavController().navigate(R.id.action_allWorkoutsFragment_to_profileFragment)
        }

        confirmDialogBuilder.setNeutralButton( getString(R.string.back)) {dialog,_ ->
            Toast.makeText(requireContext(),getString(R.string.lazy), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        confirmDialogBuilder.create().show()
    }



    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}