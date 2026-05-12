package com.example.eventos.core.repositories

import com.example.eventos.core.ResponseService
import com.example.eventos.onboarding.personal.model.UserProfile

interface UserService {
    suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit>
}