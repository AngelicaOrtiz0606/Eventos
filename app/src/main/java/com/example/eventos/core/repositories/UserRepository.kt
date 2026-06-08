package com.example.eventos.core.repositories

import com.example.eventos.core.ResponseService
import com.example.eventos.onboarding.personal.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository : UserService {
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("users")

    override suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                userCollection.document(userProfile.id)
                    .set(userProfile)
                    .await()
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("No se pudo crear el perfil: ${e.localizedMessage}")
            }
        }

    override suspend fun getUserInfo(uid: String): ResponseService<UserProfile> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = userCollection.document(uid).get().await()
                val profile = snapshot.toObject(UserProfile::class.java)
                if (profile != null) {
                    ResponseService.Success(profile)
                } else {
                    ResponseService.Error("Perfil no encontrado")
                }
            } catch (e: Exception) {
                ResponseService.Error("Error al obtener perfil: ${e.localizedMessage}")
            }
        }
}