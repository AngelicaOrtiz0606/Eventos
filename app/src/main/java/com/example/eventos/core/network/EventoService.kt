package com.example.eventos.core.network

import com.example.eventos.core.ResponseService
import com.example.eventos.core.model.Evento

interface EventoService {
    suspend fun getEventos(): ResponseService<List<Evento>>
}
