package com.example.eventos.home.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventos.core.ResponseService
import com.example.eventos.core.model.Evento
import com.example.eventos.core.network.EventoService
import com.example.eventos.core.repositories.EventoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventosViewModel (
    private val service: EventoService = EventoRepository() /* EventoService es agnostico y se le asigna el EventoRepository */
) : ViewModel() {

    private val _eventoState = MutableStateFlow<ResponseService<List<Evento>>?>(null)
    
    // Corregido: se usa ":" para el tipo y "=" para asignar el valor
    val eventoState: StateFlow<ResponseService<List<Evento>>?> = _eventoState.asStateFlow()

    fun loadEventos() {
        viewModelScope.launch {
            _eventoState.value = ResponseService.Loading
            _eventoState.value = service.getEventos()
        }
    }
}
