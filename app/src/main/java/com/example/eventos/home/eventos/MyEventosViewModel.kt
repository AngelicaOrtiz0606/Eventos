package com.example.eventos.home.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventos.core.ResponseService
import com.example.eventos.core.model.Evento
import com.example.eventos.core.network.EventoService
import com.example.eventos.core.repositories.EventoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyEventosViewModel(
    private val service: EventoService = EventoRepository()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _myEventosState = MutableStateFlow<ResponseService<List<Evento>>?>(null)
    val myEventosState: StateFlow<ResponseService<List<Evento>>?> = _myEventosState

    private var allEvents: List<Evento> = emptyList()
    private var registeredEventIds: Set<Int> = emptySet()

    fun loadMyEventos() {
        viewModelScope.launch {
            _myEventosState.value = ResponseService.Loading
            
            val user = auth.currentUser
            if (user == null) {
                _myEventosState.value = ResponseService.Error("Usuario no autenticado")
                return@launch
            }

            try {
                // 1. Obtener IDs de eventos inscritos desde Firestore
                val snapshot = db.collection("inscripciones")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()
                
                registeredEventIds = snapshot.documents.mapNotNull { it.getLong("eventoId")?.toInt() }.toSet()

                // 2. Obtener todos los eventos desde el servicio
                val result = service.getEventos()
                if (result is ResponseService.Success) {
                    allEvents = result.data
                    filterAndOrder()
                } else if (result is ResponseService.Error) {
                    _myEventosState.value = result
                }
            } catch (e: Exception) {
                _myEventosState.value = ResponseService.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun filterByDate(month: Int, year: Int) {
        filterAndOrder(month, year)
    }

    private fun filterAndOrder(month: Int? = null, year: Int? = null) {
        var filtered = allEvents.filter { it.id in registeredEventIds }

        if (year != null && year != 0) {
            filtered = filtered.filter { evento ->
                val dateStr = evento.fechaLimiteInscripcion ?: evento.fechasEvento.firstOrNull()?.fecha ?: ""
                dateStr.startsWith(year.toString())
            }
        }

        if (month != null && month != 0) {
            val monthStr = if (month < 10) "0$month" else month.toString()
            filtered = filtered.filter { evento ->
                val dateStr = evento.fechaLimiteInscripcion ?: evento.fechasEvento.firstOrNull()?.fecha ?: ""
                dateStr.contains("-$monthStr-")
            }
        }

        // Ordenar por fecha
        filtered = filtered.sortedBy { it.fechaLimiteInscripcion ?: it.fechasEvento.firstOrNull()?.fecha ?: "" }

        _myEventosState.value = ResponseService.Success(filtered)
    }
}
