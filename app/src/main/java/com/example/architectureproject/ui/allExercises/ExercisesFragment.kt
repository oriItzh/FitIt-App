package com.example.architectureproject.ui.allExercises

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
import com.example.architectureproject.R
import com.example.architectureproject.databinding.AllExercisesFragmentBinding
import com.example.architectureproject.ui.viewModels.ExerciseViewModel

class ExercisesFragment : Fragment() {

    private val viewModel : ExerciseViewModel by activityViewModels()

    private var _binding: AllExercisesFragmentBinding? = null

    private val binding get() = _binding!!

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
                        viewModel.deleteAll()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        viewModel.allExercises?.observe(viewLifecycleOwner) {

            binding.recycler.adapter = Ex_RecyclerViewAdapter(
                it, viewModel,
                object : Ex_RecyclerViewAdapter.ExerciseListener {
                    override fun onItemClicked(index: Int) {
                    }

                    override fun onItemLongClick(index: Int) {
                        viewModel.setItem(it[index])
                        findNavController().navigate(R.id.action_exercisesFragment_to_detailedExerciseFragment)

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
                confirmDialogBuilder.setMessage(getString(R.string.delete_message_exercise))
                confirmDialogBuilder.setCancelable(false)

                confirmDialogBuilder.setPositiveButton( getString(R.string.yes)) {dialog,_ ->
                    Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    viewModel.deleteExercise((binding.recycler.adapter as Ex_RecyclerViewAdapter)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AllExercisesFragmentBinding.inflate(inflater,container,false)

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        }

        binding.goBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_exercisesFragment_to_profileFragment)
        }

        binding.addPlanButton.setOnClickListener {
            findNavController().navigate(R.id.action_exercisesFragment_to_addExerciseFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}