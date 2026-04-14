package com.example.eventos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.eventos.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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
    }

    private fun validateFields() {
        val numCuenta = binding.usernameEdit.text.toString().trim()
        val password = binding.passwordEdit.text.toString().trim()

        val isAccountValid = numCuenta.length == 9
        val isPasswordValid = password.length >= 8

        // Validación número de cuenta
        if (numCuenta.isNotEmpty() && !isAccountValid) {
            binding.usernameLayout.error = "Debe tener 9 dígitos"
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
            numCuenta.isNotEmpty() &&
                    password.isNotEmpty() &&
                    isAccountValid &&
                    isPasswordValid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpValidation()
    }

}