package com.example.eventos.core.repositories

import com.example.eventos.core.ResponseService
import com.example.eventos.core.model.Evento
import com.example.eventos.core.network.ApiClient
import com.example.eventos.core.network.EventoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventoRepository : EventoService {
    private val api = ApiClient.EventoAPI

    override suspend fun getEventos(): ResponseService<List<Evento>> =
        withContext(Dispatchers.IO) {
            try {
                // Llamamos a la API
                val response = api.getEventos()
                // Si todo sale bien, devolvemos la lista de eventos dentro de Success
                ResponseService.Success(response.eventos)
            } catch (e: Exception) {
                // Si algo falla (internet, error de server, etc.), devolvemos el Error
                ResponseService.Error(e.localizedMessage ?: "Error al obtener eventos")
            }
        }
}