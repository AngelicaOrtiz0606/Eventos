package com.example.eventos.core.repositories

import com.example.eventos.core.ResponseService
import com.example.eventos.core.model.Evento
import com.example.eventos.core.model.EventoResponse
import com.example.eventos.core.network.ApiClient
import com.example.eventos.core.network.EventoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class EventoRepository : EventoService {
    private val api = ApiClient.EventoApi

    override suspend fun getEventos(): ResponseService<List<Evento>> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Obtenemos la respuesta envuelta en el objeto Response de Retrofit
                val response: Response<EventoResponse> = api.getEventos()

                // 2. Verificamos si la petición fue exitosa (código 200-299)
                if (response.isSuccessful) {
                    val body = response.body()

                    // 3. Verificamos que el cuerpo no sea nulo
                    if (body != null) {
                        // Accedemos a la lista 'eventos' definida en tu modelo EventoResponse
                        ResponseService.Success(body.eventos)
                    } else {
                        ResponseService.Error("La respuesta del servidor está vacía")
                    }
                } else {
                    // 4. Manejamos errores del servidor (ej. 404, 500) usando el código de error
                    ResponseService.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                // 5. Manejamos errores de conexión o excepciones inesperadas
                ResponseService.Error(
                    "No se pudieron cargar los eventos: ${e.localizedMessage}"
                )
            }
        }
}