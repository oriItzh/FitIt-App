package com.example.architectureproject.ui.addExercise

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
import com.example.architectureproject.ui.allExercises.MuscleGroupAdapter
import com.example.architectureproject.ui.viewModels.ExerciseViewModel
import com.example.architectureproject.ui.viewModels.MuscleGroupItemModel
import com.example.archtectureproject.data.model.Exercise

class AddExerciseFragment : Fragment() {

    private val exerciseViewModel: ExerciseViewModel by activityViewModels()
    private var _binding: AddExerciseFragmentBinding? = null
    private val binding get() = _binding!!

    private var numOfSets: Int = 3
    private var numOfReps: Int = 12
    private var weight: Double = 100.0
    private var selectedImage = R.drawable.back_1
    private lateinit var selectedMuscle: MuscleGroupItemModel
    private var muscleItems: List<MuscleGroupItemModel> = listOf<MuscleGroupItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddExerciseFragmentBinding.inflate(inflater,container,false)

        return binding.root
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

        binding.finishButton.setOnClickListener {
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
                Toast.makeText(requireContext(), getString(R.string.exercise_added), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addExerciseFragment_to_exercisesFragment)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fill_correct), Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun validatePositive(num : Double): Boolean {
        if (num > 0) return true
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedMuscle = MuscleGroupItemModel(getString(R.string.back_body), R.drawable.muscle_icon_back)

        binding.numSets.etValue.setText(numOfSets.toString())
        binding.numReps.etValue.setText(numOfReps.toString())

        val recyclerView: RecyclerView = binding.muscleTypesRecyclerview
        setUpMuscleItems()

        val adapter: MuscleGroupAdapter = MuscleGroupAdapter(muscleItems, object: MuscleGroupAdapter.MuscleGroupListener{
            override fun onItemClicked(index: Int) {
                selectedMuscle = muscleItems[index]
                binding.muscleIcon.setImageResource(selectedMuscle.imageSrc)
                binding.muscleGroup.text = selectedMuscle.muscleType
                binding.muscleTypesRecyclerview.visibility = View.GONE
            }

            override fun onItemLongClick(index: Int) {
                 Toast.makeText(requireContext(),"Long click",Toast.LENGTH_LONG).show()

            }
        })
        recyclerView.adapter = adapter
        val numberOfColumns =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 3
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
