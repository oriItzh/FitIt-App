
package com.example.architectureproject.ui.account

import android.content.Intent
import androidx.lifecycle.Observer
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.architectureproject.R
import com.example.architectureproject.databinding.AccountDialogBinding
import com.example.archtectureproject.data.model.PersonalData
import com.example.architectureproject.ui.viewModels.PersonalDataViewModel


class AccountDialog : Fragment() {

    private val viewModel : PersonalDataViewModel by activityViewModels()

    private var _binding: AccountDialogBinding? = null

    private val binding get() = _binding!!

    private var imageUri : Uri? = null

    val pickItemLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            binding.pickedImage.setImageURI(it)
            if (it != null) {
                requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            imageUri = it
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AccountDialogBinding.inflate(inflater, container,false)

        viewModel.getOnlyPerson()?.observe(viewLifecycleOwner, Observer { person ->
            person?.let {
                binding.firstNameEt.setText(it.firstName)
                binding.surnameEt.setText(it.surname)
                binding.ageEt.setText(it.age)
                binding.heightEt.setText(it.height)
                binding.weightEt.setText(it.weight)

                if (it.gender == "Male")
                    binding.sex.check(R.id.male)
                else
                    binding.sex.check(R.id.female)
                imageUri = Uri.parse(it.photo)

                binding.pickedImage.setImageURI(imageUri)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goBack.setOnClickListener {
            findNavController().navigate(R.id.action_accountDialog_to_profileFragment)
        }

        // Change the edit texts to dark blue when clicked

        binding.firstNameEt.setOnFocusChangeListener {_, clicked ->
            if (clicked) {
                binding.firstNameLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
            }
        }

        binding.surnameEt.setOnFocusChangeListener {_, clicked ->
            if (clicked) {
                binding.surnameLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
            }
        }

        binding.ageEt.setOnFocusChangeListener {_, clicked ->
            if (clicked) {
                binding.ageLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
            }
        }

        binding.heightEt.setOnFocusChangeListener {_, clicked ->
            if (clicked) {
                binding.heightLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
            }
        }

        binding.weightEt.setOnFocusChangeListener {_, clicked ->
            if (clicked) {
                binding.weightLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.dark_blue))
            }
        }

        // pick a profile image
        binding.imagePicker.setOnClickListener {
            pickItemLauncher.launch(arrayOf("image/*"))
        }

        binding.submit.setOnClickListener {
            val firstName = binding.firstNameEt.text.toString()
            val surname = binding.surnameEt.text.toString()
            val height = binding.heightEt.text.toString()
            val age = binding.ageEt.text.toString()
            val weight = binding.weightEt.text.toString()

            // Validations
            if (!validName(firstName)) {
                binding.unvalidInput.visibility = View.VISIBLE
                binding.unvalidInput.text = getString(R.string.valid_name)
                binding.firstNameLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else if (!validSurname(surname)) {
                binding.unvalidInput.visibility = View.VISIBLE
                binding.unvalidInput.text =getString(R.string.valid_surname)
                binding.surnameLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else if (!validAge(age)) {
                binding.unvalidInput.visibility = View.VISIBLE
                binding.unvalidInput.text =getString(R.string.valid_age)
                binding.ageLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else if (!validHeight(height)) {
                binding.unvalidInput.visibility = View.VISIBLE
                binding.unvalidInput.text =getString(R.string.valid_height)
                binding.heightLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else if (!validWeight(weight)) {
                binding.unvalidInput.visibility = View.VISIBLE
                binding.unvalidInput.text =getString(R.string.valid_weight)
                binding.weightLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else {
                viewModel.deleteAll()
                binding.unvalidInput.visibility = View.GONE

                val radioGroup: RadioGroup = binding.sex
                val checkedRadioButtonId: Int = radioGroup.checkedRadioButtonId
                val checkedRadioButton: RadioButton =
                    binding.root.findViewById(checkedRadioButtonId)
                val gender: String = checkedRadioButton.text.toString()

                if (imageUri == null)
                    imageUri = Uri.parse("android.resource://${context?.packageName}/${R.drawable.empty_profile}")

                // Updates the personal data
                val details = PersonalData(
                    firstName,
                    surname,
                    age,
                    height,
                    weight,
                    gender,
                    imageUri.toString()
                )
                viewModel.addItem(details)

                findNavController().navigate(R.id.action_accountDialog_to_profileFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Validiation for age
     */
    private fun validAge(age: String) : Boolean {
        val ageValue = age.toIntOrNull()
        if (ageValue == null)
            return false
        if (ageValue < 15 || ageValue >= 100)
            return false
        return true
    }

    /**
     * Validation for weight
     */
    private fun validWeight(weight: String) : Boolean {
        val weightValue = weight.toIntOrNull()
        if (weightValue == null)
            return false
        if (weightValue < 30 || weightValue >= 200)
            return false
        return true
    }

    /**
     * Validation for height
     */
    private fun validHeight(height: String) : Boolean {
        val heightValue = height.toIntOrNull()
        if (heightValue == null)
            return false
        if (heightValue < 100 || heightValue >= 250)
            return false
        return true
    }

    /**
     * Validation for name
     */
    private fun validName(name: String) : Boolean {
        if (!name.matches(Regex("^\\p{L}{2,10}\$")))
                return false
        if (name.length < 2 || name.length > 10)
            return false
        return true
    }

    /**
     * Validation for surname
     */
    private fun validSurname(name: String) : Boolean {
        return name.matches(Regex("^\\p{L}+\$"))
    }

}