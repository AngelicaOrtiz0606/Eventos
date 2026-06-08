package com.example.eventos.home.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventos.core.ResponseService
import com.example.eventos.core.repositories.UserRepository
import com.example.eventos.onboarding.personal.model.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AccountViewModel : ViewModel() {
    private val repository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _profileState = MutableStateFlow<ResponseService<UserProfile>?>(null)
    val profileState: StateFlow<ResponseService<UserProfile>?> = _profileState

    private val _passwordChangeState = MutableStateFlow<ResponseService<Unit>?>(null)
    val passwordChangeState: StateFlow<ResponseService<Unit>?> = _passwordChangeState

    fun loadProfile() {
        val user = auth.currentUser
        if (user == null) {
            _profileState.value = ResponseService.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            _profileState.value = ResponseService.Loading
            _profileState.value = repository.getUserInfo(user.uid)
        }
    }

    fun updatePassword(currentPass: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email == null) {
            _passwordChangeState.value = ResponseService.Error("Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            _passwordChangeState.value = ResponseService.Loading
            try {
                // Re-autenticación obligatoria para operaciones sensibles en Firebase
                val credential = EmailAuthProvider.getCredential(email, currentPass)
                user.reauthenticate(credential).await()
                
                // Actualizar contraseña
                user.updatePassword(newPassword).await()
                _passwordChangeState.value = ResponseService.Success(Unit)
            } catch (e: Exception) {
                _passwordChangeState.value = ResponseService.Error(e.localizedMessage ?: "Error al cambiar contraseña")
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
