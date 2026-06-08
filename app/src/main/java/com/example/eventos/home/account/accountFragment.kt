package com.example.eventos.home.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.eventos.core.FragmentCommunicator
import com.example.eventos.core.ResponseService
import com.example.eventos.databinding.DialogChangePasswordBinding
import com.example.eventos.databinding.FragmentAccountBinding
import com.example.eventos.onboarding.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class accountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AccountViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupListeners()
        observeState()
        viewModel.loadProfile()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres salir?")
                .setPositiveButton("Salir") { _, _ ->
                    viewModel.logout()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)
        
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Actualizar") { _, _ ->
                val currentPass = dialogBinding.currentPasswordEdit.text.toString().trim()
                val newPass = dialogBinding.newPasswordEdit.text.toString().trim()

                if (currentPass.isNotEmpty() && newPass.isNotEmpty()) {
                    if (newPass.length >= 6) {
                        viewModel.updatePassword(currentPass, newPass)
                    } else {
                        Snackbar.make(binding.root, "La nueva contraseña debe tener al menos 6 caracteres", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(binding.root, "Ambos campos son requeridos", Snackbar.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar carga de perfil
                launch {
                    viewModel.profileState.collect { state ->
                        when (state) {
                            is ResponseService.Loading -> communicator.manageLoader(true)
                            is ResponseService.Success -> {
                                communicator.manageLoader(false)
                                val profile = state.data
                                binding.tvHeaderName.text = "${profile.firstName} ${profile.lastName}"
                                binding.tvProfileFirstName.text = profile.firstName
                                binding.tvProfileLastName.text = profile.lastName
                                binding.tvProfileEmail.text = FirebaseAuth.getInstance().currentUser?.email ?: ""
                                binding.tvProfilePhone.text = if (profile.phone.isNotBlank()) profile.phone else "No registrado"
                                binding.tvProfileBirthDate.text = if (profile.birthDate.isNotBlank()) profile.birthDate else "No registrada"
                                binding.tvProfileStudentId.text = if (profile.studentId.isNotBlank()) profile.studentId else "No registrado"
                            }
                            is ResponseService.Error -> {
                                communicator.manageLoader(false)
                                Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                            }
                            null -> {}
                        }
                    }
                }

                // Observar cambio de contraseña
                launch {
                    viewModel.passwordChangeState.collect { state ->
                        when (state) {
                            is ResponseService.Loading -> communicator.manageLoader(true)
                            is ResponseService.Success -> {
                                communicator.manageLoader(false)
                                Snackbar.make(binding.root, "Contraseña actualizada correctamente", Snackbar.LENGTH_LONG).show()
                            }
                            is ResponseService.Error -> {
                                communicator.manageLoader(false)
                                Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                            }
                            null -> {}
                        }
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
