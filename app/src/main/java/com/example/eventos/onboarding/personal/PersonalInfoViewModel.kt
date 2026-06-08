package com.example.eventos.onboarding.personal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventos.core.ResponseService
import com.example.eventos.core.repositories.UserRepository
import com.example.eventos.onboarding.personal.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _saveState = MutableStateFlow<ResponseService<Unit>?>(null)
    val saveState: StateFlow<ResponseService<Unit>?> = _saveState.asStateFlow()

    fun validateFirstName(value: String): String? {
        if (value.isBlank()) return "El nombre es requerido"
        if (value.length < 2) return "Mínimo 2 caracteres"
        if (!value.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
        return null
    }

    fun validateLastName(value: String): String? {
        if (value.isBlank()) return "Los apellidos son requeridos"
        if (value.length < 2) return "Mínimo 2 caracteres"
        if (!value.all { it.isLetter() || it.isWhitespace() }) return "Solo se permiten letras"
        return null
    }

    fun validatePhone(value: String): String? {
        if (value.isBlank()) return "El teléfono es requerido"
        if (!value.all { it.isDigit() }) return "Solo números"
        if (value.length !in 10..15) return "Entre 10 y 15 dígitos"
        return null
    }

    fun validateStudentId(value: String): String? {
        if (value.isBlank()) return "El número de cuenta es requerido"
        if (value.length != 9) return "Debe tener exactamente 9 dígitos"
        if (!value.all { it.isDigit() }) return "Solo números"
        return null
    }

    fun validateBirthDate(value: String): String? {
        if (value.isBlank()) return "Selecciona tu fecha de nacimiento"
        return null
    }

    fun isFormValid(firstName: String, lastName: String, phone: String, studentId: String, birthDate: String): Boolean {
        return validateFirstName(firstName) == null &&
                validateLastName(lastName) == null &&
                validatePhone(phone) == null &&
                validateStudentId(studentId) == null &&
                validateBirthDate(birthDate) == null
    }

    fun saveProfile(uid: String, firstName: String, lastName: String, phone: String, studentId: String, birthDate: String) {
        viewModelScope.launch {
            _saveState.value = ResponseService.Loading
            val user = UserProfile(
                id = uid, 
                firstName = firstName, 
                lastName = lastName, 
                phone = phone, 
                studentId = studentId,
                birthDate = birthDate
            )
            _saveState.value = repository.saveUserInfo(user)
        }
    }
}
