package com.example.architectureproject.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.example.architectureproject.ui.viewModels.PersonalDataViewModel
import java.util.*
import com.example.architectureproject.R
import com.example.architectureproject.databinding.ProfileFragmentBinding
import com.example.architectureproject.ui.allworkouts.WorkoutAdapter
import com.example.architectureproject.ui.viewModels.HistoryViewModel
import com.example.architectureproject.ui.viewModels.TimerViewModel
import com.example.architectureproject.ui.viewModels.WorkoutViewModel
import com.example.archtectureproject.data.model.History
import java.util.concurrent.TimeUnit


class ProfileFragment : Fragment() {

    private val viewModelPersonal : PersonalDataViewModel by activityViewModels()
    private val viewModelHistory : HistoryViewModel by activityViewModels()
    private val viewModelTimer : TimerViewModel by activityViewModels()
    private val viewModelWorkout : WorkoutViewModel by activityViewModels()

    private var _binding: ProfileFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ProfileFragmentBinding.bind(view)

        // permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // Requesting the permissions
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS),
                REQUEST_PERMISSIONS)
        }

        // Updates the details from the account fragment
        viewModelPersonal.personalData?.observe(viewLifecycleOwner) { personalData ->
            personalData?.let {
                // Updates the name
                binding.profileName.text = it.firstName

                // Updates the image
                it.photo?.let { photoUri ->
                    binding.profileImage.setImageURI(Uri.parse(photoUri))
                }

                val weightString = it.weight + "  " + getString(R.string.kg)
                val heightString =
                    String.format("%.2f", it.height.toFloat() / 100) + "  " + getString(R.string.m)

                // Updates age, height, and weight
                binding.ageInfo.text = it.age
                binding.weightInfo.text = weightString
                binding.heightInfo.text = heightString

                binding.infoUser.visibility = View.VISIBLE
                binding.activityLayout.visibility = View.VISIBLE
                binding.accountButton.text = getString(R.string.update_account)
            }
        }

        binding.accountButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountDialog)
        }

        binding.addInformationButton.setOnClickListener {
            // Show the dialog and pass the list of workouts to it
            showAddInfoDialog()
        }

        //Fetching Data: When a date is selected on the calendar, fetch the history data for that specific date and update the LiveData
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth -> // triggers whenever a date is selected on the calendar
            val calendar = Calendar.getInstance()
            calendar.set(
                year,
                month,
                dayOfMonth
            ) // A Calendar instance is created and set to the selected year, month, and day.

            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            binding.selctedDate.text = "$dayOfMonth/${month + 1}"

            // The fetchHistoryByDate
            viewModelHistory.fetchHistoryByDate(selectedDate)?.observe(viewLifecycleOwner){
                if (it != null) {

                    if (isToday(calendar)) {
                        binding.remainder.visibility = View.VISIBLE
                        binding.workoutToday.text = it.workout?.title
                        binding.timeToday.text = it.time
                    }
                    if (selectedDate == it.date) {
                        binding.deleteWorkout.visibility = View.VISIBLE
                        binding.intensity.visibility = View.VISIBLE
                        binding.time.visibility = View.VISIBLE
                        binding.workoutInfo.text =
                            it.workout?.title  // Use the workout's title
                        binding.timeInfo.text = it.time
                        binding.intensityInfo.text = it.intensity_level.toString()
                    }

                    // delete the workout
                    binding.deleteWorkout.setOnClickListener {
                        val confirmDialogBuilder = AlertDialog.Builder(requireContext())
                        confirmDialogBuilder.setMessage(getString(R.string.delete_message_workout))
                        confirmDialogBuilder.setCancelable(false)

                        confirmDialogBuilder.setPositiveButton( getString(R.string.yes)) {dialog,_ ->
                            dialog.dismiss()
                            binding.time.visibility = View.GONE
                            binding.intensity.visibility = View.GONE
                            binding.workoutInfo.text = getString(R.string.relax)
                            binding.deleteWorkout.visibility = View.GONE
                            if (isToday(calendar)) {
                                binding.remainder.visibility = View.GONE
                            }
                            viewModelHistory.deleteHistoryByDate(selectedDate)
                        }
                        confirmDialogBuilder.setNeutralButton( getString(R.string.no)) {dialog,_ ->
                            dialog.dismiss()
                        }
                        confirmDialogBuilder.create().show()
                    }

                }
                else {
                    binding.intensity.visibility = View.GONE
                    binding.time.visibility = View.GONE
                    binding.workoutInfo.text = getString(R.string.relax)
                    binding.deleteWorkout.visibility = View.GONE
                }
            }
        }

        // timer

        viewModelTimer.timerValue.observe(viewLifecycleOwner) {
            binding.timer.text = formatTime(it)
        }

        binding.stopButton.setOnClickListener {
            viewModelTimer.stopTimer()
            binding.menuContent.visibility = View.VISIBLE
            binding.timerLayout.visibility = View.GONE
            Toast.makeText(requireContext(),getString(R.string.workout_finished), Toast.LENGTH_SHORT).show()
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        binding.pauseResumeButton.setOnClickListener {
            if (viewModelTimer.isPaused()) {
                viewModelTimer.resumeTimer()
                binding.pauseResumeButton.text = getString(R.string.pause_activity)
                activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                viewModelTimer.pauseTimer()
                binding.pauseResumeButton.text = getString(R.string.resume_activity)
                activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Timer / Main menu
        if (viewModelTimer.isActive()){
            binding.menuContent.visibility = View.GONE
            binding.timerLayout.visibility = View.VISIBLE
            viewModelWorkout.chosenWorkout.observe(viewLifecycleOwner){
                binding.activeWorkoutName.text = it.title
            }
        }

        // open contacts when click on phone
        binding.call.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_SELECT_CONTACT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater,container,false)

        binding.exercisesButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_exercisesFragment)
        }

        binding.workoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_allWorkoutsFragment)
        }

        binding.gymButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_mapFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Exit the app
                requireActivity().finish()
            }
        })


        // Set the calendar to today's date
        val calendarView = binding.calendarView
        val today = Calendar.getInstance()
        calendarView.date = today.timeInMillis

        // initial day in calender - show remainder if it has a workout
        val todayDate = Calendar.getInstance()
        val initialDate = "${todayDate.get(Calendar.DAY_OF_MONTH)}/${todayDate.get(Calendar.MONTH) + 1}/${todayDate.get(Calendar.YEAR)}"
        binding.selctedDate.text = "${todayDate.get(Calendar.DAY_OF_MONTH)}/${todayDate.get(Calendar.MONTH) + 1}"
        viewModelHistory.fetchHistoryByDate(initialDate)?.observe(viewLifecycleOwner) {
            if (it != null) {
                if (isToday(todayDate)) {
                    binding.remainder.visibility = View.VISIBLE
                    binding.workoutToday.text = it.workout?.title
                    binding.timeToday.text = it.time
                } else {
                    binding.remainder.visibility = View.GONE
                }

                if (initialDate == it.date) {
                    binding.deleteWorkout.visibility = View.VISIBLE
                    binding.workoutInfo.text = it.workout?.title
                    binding.timeInfo.text = it.time
                    binding.intensityInfo.text = it.intensity_level.toString()
                    binding.time.visibility = View.VISIBLE
                    binding.intensity.visibility = View.VISIBLE
                }

            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth -> // triggers whenever a date is selected on the calendar
            val calendar = Calendar.getInstance()
            calendar.set(
                year,
                month,
                dayOfMonth
            ) // A Calendar instance is created and set to the selected year, month, and day.

            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            binding.selctedDate.text = "$dayOfMonth/${month + 1}"

            // The fetchHistoryByDate
            viewModelHistory.fetchHistoryByDate(selectedDate)?.observe(viewLifecycleOwner) {
                if (it != null) {

                    if (isToday(calendar)) {
                        binding.remainder.visibility = View.VISIBLE
                        binding.workoutToday.text = it.workout?.title
                        binding.timeToday.text = it.time
                    }
                    if (selectedDate == it.date) {
                        binding.deleteWorkout.visibility = View.VISIBLE
                        binding.intensity.visibility = View.VISIBLE
                        binding.time.visibility = View.VISIBLE
                        binding.workoutInfo.text =
                            it.workout?.title  // Use the workout's title
                        binding.timeInfo.text = it.time
                        binding.intensityInfo.text = it.intensity_level.toString()
                    }

                    // delete the workout
                    binding.deleteWorkout.setOnClickListener {
                        val confirmDialogBuilder = AlertDialog.Builder(requireContext())
                        confirmDialogBuilder.setMessage(getString(R.string.delete_message_workout))
                        confirmDialogBuilder.setCancelable(false)

                        confirmDialogBuilder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                            dialog.dismiss()
                            binding.time.visibility = View.GONE
                            binding.intensity.visibility = View.GONE
                            binding.workoutInfo.text = getString(R.string.relax)
                            binding.deleteWorkout.visibility = View.GONE
                            if (isToday(calendar)) {
                                binding.remainder.visibility = View.GONE
                            }
                            viewModelHistory.deleteHistoryByDate(selectedDate)
                        }
                        confirmDialogBuilder.setNeutralButton(getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        confirmDialogBuilder.create().show()
                    }

                } else {
                    binding.intensity.visibility = View.GONE
                    binding.time.visibility = View.GONE
                    binding.workoutInfo.text = getString(R.string.relax)
                    binding.deleteWorkout.visibility = View.GONE
                }
            }
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        val millis = milliseconds % 1000
        return String.format("%02d:%02d:%03d", minutes, seconds, millis)
    }

    // Fetches the list of workouts from the WorkoutViewModel.
    private fun showAddInfoDialog() {
        viewModelWorkout.items?.observe(viewLifecycleOwner) { workouts ->
            val dialogFragment = AddInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("workouts", ArrayList(workouts))  // Pass the entire Workout objects
                }
            }
            dialogFragment.show(childFragmentManager, "AddInfoDialogFragment")
        }
    }

    private fun isToday(date: Calendar): Boolean {
        val today = Calendar.getInstance()
        return date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                date.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                date.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val number = getContactNumber(uri)
                number?.let {
                    makePhoneCall(it)
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getContactNumber(uri: Uri): String? {
        var number: String? = null
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val hasPhoneNumber = it.getString(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt()
                if (hasPhoneNumber > 0) {
                    val cursorPhone = requireActivity().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(contactId),
                        null
                    )
                    if (cursorPhone?.moveToFirst() == true) {
                        number = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    }
                    cursorPhone?.close()
                }
            }
            it.close()
        }
        return number
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        }
    }

    companion object {
        private const val REQUEST_SELECT_CONTACT = 1
        private const val REQUEST_PERMISSIONS = 100
    }
}
