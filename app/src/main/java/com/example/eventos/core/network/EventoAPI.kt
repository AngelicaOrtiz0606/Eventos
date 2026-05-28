package com.example.eventos.core.network

import com.example.eventos.core.model.EventoResponse
import retrofit2.Response
import retrofit2.http.GET

interface EventoAPI {
    @GET("/")
    suspend fun getEventos(): Response<EventoResponse>
}