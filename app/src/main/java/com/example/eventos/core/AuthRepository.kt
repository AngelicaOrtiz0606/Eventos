package com.example.eventos.core

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(): Authentication {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    override suspend fun requestLogin(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { ResponseService.Success(it)}
                ?: ResponseService.Error("Usuario no encontrado")
        } catch (e: FirebaseAuthInvalidCredentialsException){
            ResponseService.Error("Número de cuenta o contraseña incorrectos")
            } catch (e: FirebaseAuthException) {
            ResponseService.Error(e.localizedMessage ?: "Ocurrió un error de autenticación")
        } catch (e: Exception) {
            ResponseService.Error("Ocurrió un error inesperado")
        }
    }

    override suspend fun requestSignUp(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { ResponseService.Success(it)}
                ?: ResponseService.Error("No se pudo crear el usuario")
        } catch (e: FirebaseAuthUserCollisionException){
            ResponseService.Error("Este número de cuenta ya está registrado")
        } catch (e: FirebaseAuthWeakPasswordException) {
            ResponseService.Error("La contraseña es muy débil")
        } catch (e: Exception) {
            ResponseService.Error("Ocurrió un error inesperado. Intente de nuevo")
        }
    }
}