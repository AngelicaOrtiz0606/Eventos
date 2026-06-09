package com.example.eventos.onboarding.forgot

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.eventos.core.FragmentCommunicator
import com.example.eventos.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        communicator = requireActivity() as FragmentCommunicator
        
        setupListeners()
        
        return binding.root
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendButton.setOnClickListener {
            val email = binding.emailEdit.text.toString().trim()

            if (validateEmail(email)) {
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            binding.emailLayout.error = "El correo es obligatorio"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Ingresa un correo válido"
            return false
        }
        binding.emailLayout.error = null
        return true
    }

    private fun sendPasswordResetEmail(email: String) {
        communicator.manageLoader(true)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                communicator.manageLoader(false)
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "Correo de recuperación enviado", Snackbar.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    Snackbar.make(binding.root, "Error: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
