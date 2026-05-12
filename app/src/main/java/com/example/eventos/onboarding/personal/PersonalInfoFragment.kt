package com.example.eventos.onboarding.personal

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.eventos.core.FragmentCommunicator
import com.example.eventos.core.ResponseService
import com.example.eventos.databinding.FragmentPersonalInfoBinding
import com.example.eventos.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PersonalInfoViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        communicator.manageLoader(false)
        setupValidation()
        setupDatePicker()
        setupClickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.continueButton.isEnabled = false
        binding.firstNameEdit.addTextChangedListener { validateAndEnable() }
        binding.lastNameEdit.addTextChangedListener { validateAndEnable() }
        binding.phoneEdit.addTextChangedListener { validateAndEnable() }
        binding.dobEdit.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val firstName = binding.firstNameEdit.text.toString().trim()
        val lastName = binding.lastNameEdit.text.toString().trim()
        val phone = binding.phoneEdit.text.toString().trim()
        val birthDate = binding.dobEdit.text.toString().trim()

        binding.firstNameLayout.error = viewModel.validateFirstName(firstName)
        binding.lastNameLayout.error = viewModel.validateLastName(lastName)
        binding.phoneLayout.error = viewModel.validatePhone(phone)
        binding.dobLayout.error = viewModel.validateBirthDate(birthDate)

        binding.continueButton.isEnabled =
            viewModel.isFormValid(firstName, lastName, phone, birthDate)
    }

    private fun setupDatePicker() {
        binding.dobEdit.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.dobEdit.setText("%02d/%02d/%04d".format(day, month + 1, year))
                },
                cal.get(Calendar.YEAR) - 18,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }
    }

    private fun setupClickListeners() {
        binding.continueButton.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Snackbar.make(binding.root, "Sesión inválida", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.saveProfile(
                uid = uid,
                firstName = binding.firstNameEdit.text.toString().trim(),
                lastName = binding.lastNameEdit.text.toString().trim(),
                phone = binding.phoneEdit.text.toString().trim(),
                birthDate = binding.dobEdit.text.toString().trim()
            )
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.continueButton.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            val intent = Intent(requireContext(), HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.continueButton.isEnabled = true
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}