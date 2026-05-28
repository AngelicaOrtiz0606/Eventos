package com.example.eventos.home.eventos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.eventos.core.FragmentCommunicator
import com.example.eventos.core.ResponseService
import com.example.eventos.databinding.FragmentEventoBinding // Nombre corregido
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class EventosFragment : Fragment() {

    private var _binding: FragmentEventoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EventosViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventoBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator // Corregido typo
        observeState()
        viewModel.loadEventos()
        return binding.root
    }

    fun observeState(){ // Cambiado a private y llaves corregidas
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.eventoState.collect { state ->
                    when(state){
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            Log.i("EventosFragment", "Eventos List: ${state.data}")
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}