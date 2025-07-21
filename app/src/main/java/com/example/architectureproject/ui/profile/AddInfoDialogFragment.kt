package com.example.architectureproject.ui.profile

import com.example.architectureproject.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.architectureproject.databinding.AddInfoDialogBinding
import com.example.architectureproject.ui.viewModels.HistoryViewModel
import com.example.archtectureproject.data.model.History
import com.example.archtectureproject.data.model.Workout
import java.util.Calendar

class AddInfoDialogFragment : DialogFragment() {

    private var _binding: AddInfoDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by activityViewModels()
    private lateinit var workouts: List<Workout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddInfoDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.plus.setOnClickListener {
            var intensity = binding.intensityInfo.text.toString().toInt()
            // Increase
            if (intensity < 5) {
                intensity++
                binding.intensityInfo.text = intensity.toString()

            }
        }

        binding.minus.setOnClickListener {
            var intesity = binding.intensityInfo.text.toString().toInt()
            // Decrease
            if (intesity > 1) {
                intesity--
                binding.intensityInfo.text = intesity.toString()
            }
        }

        // Set up the date picker
        binding.dateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // Update the button text with the selected date
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    binding.dateBtn.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Button for choosing a time
        val timeBtn = binding.timeBtn

        // Sets the button text to the chosen time
        val listenerTime = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            var time = "0"
            // Makes same pattern of 4 digits
            if (minute < 10) {
                if (hour < 10)
                    time = "0$hour:0$minute"
                else
                    time = "$hour:0$minute"
            } else if (hour < 10) {
                time = "0$hour:$minute"
            } else {
                time = "$hour:$minute"
            }
            timeBtn.text = time
        }

        // Determines the chosen time when the button is clicked
        timeBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val timeDialog = TimePickerDialog(
                requireContext(),
                listenerTime,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
            )
            timeDialog.show()
        }

        // Get the workouts from the arguments
        workouts = arguments?.getParcelableArrayList<Workout>("workouts") ?: listOf()

        if (workouts.isEmpty()){
            binding.scroll.visibility = View.GONE
            binding.noWorkouts.visibility = View.VISIBLE
        } else {
            binding.scroll.visibility = View.VISIBLE
            binding.noWorkouts.visibility = View.GONE
        }

        // Set up the Spinner with workout titles
        val workoutTitles = workouts.map { it.title }
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, workoutTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.workoutSpinner.adapter = adapter

        binding.done.setOnClickListener {
            val date = binding.dateBtn.text.toString()
            val time = binding.timeBtn.text.toString()
            val selectedWorkoutIndex = binding.workoutSpinner.selectedItemPosition
            val workout = if (selectedWorkoutIndex >= 0) workouts[selectedWorkoutIndex] else null
            val intensity = binding.intensityInfo.text.toString().toIntOrNull()

            if (date.isNotEmpty() && time != getString(R.string.time) && workout != null && intensity != null) {
                viewModel.deleteHistoryByDate(date)
                val newHistory = History(
                    date = date,
                    time = time,
                    workout = workout,
                    intensity_level = intensity
                )
                viewModel.addHistory(newHistory)
                dismiss()
            } else {
                // Handle validation error
                Toast.makeText(requireContext(), getString(R.string.fill_correct), Toast.LENGTH_SHORT).show()
            }
        }

        binding.goBack.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
