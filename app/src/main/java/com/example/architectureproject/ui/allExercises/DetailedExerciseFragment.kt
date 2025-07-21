package com.example.architectureproject.ui.allExercises

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
import androidx.recyclerview.widget.RecyclerView
import com.example.architectureproject.R
import com.example.architectureproject.databinding.AddExerciseFragmentBinding
import com.example.architectureproject.databinding.DetailedExerciseBinding
import com.example.archtectureproject.data.model.Exercise
import com.example.architectureproject.ui.viewModels.ExerciseViewModel
import com.example.architectureproject.ui.viewModels.MuscleGroupItemModel
import com.example.architectureproject.ui.allExercises.MuscleGroupAdapter
import com.example.architectureproject.utils.autoCleared

class DetailedExerciseFragment : Fragment() {

    //    for debugging
    private var binding: DetailedExerciseBinding by autoCleared()
    private val exerciseViewModel: ExerciseViewModel by activityViewModels()
    private var muscleItems: List<MuscleGroupItemModel> = listOf<MuscleGroupItemModel>()
    private lateinit var selectedMuscle: MuscleGroupItemModel
    private var numOfReps = 10
    private var numOfSets = 3
    private var weight = 100.0
    private lateinit var adapter : MuscleGroupAdapter
    private lateinit var currentExercise: Exercise


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailedExerciseBinding.inflate(inflater,container,false)
        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_detailedExerciseFragment_to_exercisesFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exerciseViewModel.chosenItem.observe(viewLifecycleOwner) {
            currentExercise = it
            selectedMuscle = MuscleGroupItemModel(it.muscleGroup, it.iconUri)
            numOfSets = it.sets!!
            numOfReps = it.reps!!
            weight = it.weight!!.toDouble()
            binding.exerciseTitleEt.setText(it.name)
            binding.numReps.etValue.setText(numOfReps.toString())
            binding.numSets.etValue.setText(numOfSets.toString())
            binding.weightNum.etValue.setText(weight.toString())
            binding.muscleIcon.setImageResource(it.iconUri)
            binding.muscleGroup.setText(it.muscleGroup)

            val recyclerView: RecyclerView = binding.muscleTypesRecyclerview
            setUpMuscleItems()
            adapter = MuscleGroupAdapter(muscleItems, object: MuscleGroupAdapter.MuscleGroupListener{
                override fun onItemClicked(index: Int) {
                    selectedMuscle = muscleItems[index]
                    binding.muscleIcon.setImageResource(selectedMuscle.imageSrc)
                    binding.muscleGroup.text = selectedMuscle.muscleType
                    binding.muscleTypesRecyclerview.visibility = View.GONE
                }

                override fun onItemLongClick(index: Int) {

                }
            })
            recyclerView.adapter = adapter
            val numberOfColumns =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 3
            recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

            setOnClickListeners()
        }

        binding.updateButton.setOnClickListener {
            exerciseViewModel.deleteExercise(currentExercise)
            val name = binding.exerciseTitleEt.text.toString()
            val weight = binding.weightNum.etValue.text.toString().toDoubleOrNull()
            val sets = binding.numSets.etValue.text.toString().toIntOrNull()
            val reps = binding.numReps.etValue.text.toString().toIntOrNull()

            if (name.isNotEmpty() && weight != null && sets != null && reps != null) {
                val exercise = Exercise(
                    name = name,
                    muscleGroup = selectedMuscle.muscleType,
                    iconUri = selectedMuscle.imageSrc,
                    weight = weight.toString(),
                    sets = sets,
                    reps = reps
                )
                exerciseViewModel.addExercise(exercise)
                Toast.makeText(requireContext(), getString(R.string.exercise_updated), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_detailedExerciseFragment_to_exercisesFragment)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fill_correct), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setUpMuscleItems() {
        val muscleNames: Array<String> = resources.getStringArray(R.array.muscle_groups)
        val muscleIcons = intArrayOf(
            R.drawable.muscle_icon_abductors,
            R.drawable.muscle_icon_abs,
            R.drawable.muscle_icon_back,
            R.drawable.muscle_icon_biceps,
            R.drawable.muscle_icon_chest,
            R.drawable.muscle_icon_forearms,
            R.drawable.muscle_icon_front_legs,
            R.drawable.muscle_icon_glutes,
            R.drawable.muscle_icon_lower_back,
            R.drawable.muscle_icon_quads,
            R.drawable.muscle_icon_shoulders,
            R.drawable.muscle_icon_side_legs,
            R.drawable.muscle_icon_triceps,
            R.drawable.muscle_icon_upper_back
        )
        for (i in 0..muscleIcons.size -1){
            muscleItems += MuscleGroupItemModel(muscleNames[i], muscleIcons[i])
        }
    }

    private fun setOnClickListeners() {
        binding.muscleIcon.setOnClickListener {
            binding.muscleTypesRecyclerview.visibility = View.VISIBLE
        }
        binding.numSets.btnPlus.setOnClickListener {
            numOfSets += 1
            binding.numSets.etValue.text = numOfSets.toString()
        }
        binding.numReps.btnPlus.setOnClickListener {
            numOfReps += 1
            binding.numReps.etValue.text = numOfReps.toString()
        }
        binding.weightNum.btnPlus.setOnClickListener {
            weight += 5
            binding.weightNum.etValue.text = weight.toString()
        }
        binding.numSets.btnMinus.setOnClickListener {
            if (validatePositive(numOfSets.toDouble())) {
                numOfSets -= 1
                binding.numSets.etValue.text = numOfSets.toString()
            }
        }
        binding.numReps.btnMinus.setOnClickListener {
            if (validatePositive(numOfReps.toDouble())){
                numOfReps -= 1
                binding.numReps.etValue.text = numOfReps.toString()
            }
        }
        binding.weightNum.btnMinus.setOnClickListener {
            if (validatePositive(weight)) {
                weight -= 5
                binding.weightNum.etValue.text = weight.toString()
            }
        }
    }

    private fun validatePositive(num : Double): Boolean {
        if (num > 0) return true
        return false
    }

}

