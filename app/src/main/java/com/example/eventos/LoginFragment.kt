package com.example.eventos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.eventos.core.FragmentCommunicator
import com.example.eventos.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()

    private lateinit var communicator: FragmentCommunicator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        communicator.manageLoader(true)

        binding.registerText.setOnClickListener {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    private fun setUpValidation(){
        binding.loginButton.isEnabled = false

        binding.usernameEdit.addTextChangedListener {
            validateFields()
        }

        binding.passwordEdit.addTextChangedListener {
            validateFields()
        }

        viewModel.requestLogin()
    }

    private fun validateFields() {
        val email = binding.usernameEdit.text.toString().trim()
        val password = binding.passwordEdit.text.toString().trim()

        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 8

        // Validación correo electrónico
        if (email.isNotEmpty() && !isEmailValid) {
            binding.usernameLayout.error = "Correo electrónico no válido"
        } else {
            binding.usernameLayout.error = null
        }

        // Validación contraseña
        if (password.isNotEmpty() && !isPasswordValid) {
            binding.passwordLayout.error = "Mínimo 8 caracteres"
        } else {
            binding.passwordLayout.error = null
        }

        // Activar botón solo si todo es válido
        binding.loginButton.isEnabled =
            email.isNotEmpty() &&
                    password.isNotEmpty() &&
                    isEmailValid &&
                    isPasswordValid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpValidation()
    }

}