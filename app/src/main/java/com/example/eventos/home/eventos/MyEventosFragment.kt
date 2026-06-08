package com.example.eventos.home.eventos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventos.R
import com.example.eventos.core.FragmentCommunicator
import com.example.eventos.core.ResponseService
import com.example.eventos.databinding.FragmentMyEventosBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MyEventosFragment : Fragment() {

    private var _binding: FragmentMyEventosBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MyEventosViewModel>()
    private lateinit var communicator: FragmentCommunicator

    private val adapter = EventosAdapter { evento ->
        val bundle = Bundle().apply {
            putParcelable("evento", evento)
        }
        findNavController().navigate(R.id.action_myEventosFragment_to_eventoDetailFragment, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyEventosBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupRecyclerView()
        setupFilters()
        observeState()
        viewModel.loadMyEventos()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvMyEventos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyEventos.adapter = adapter
    }

    private fun setupFilters() {
        val months = arrayOf("Todos los meses", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
        val years = arrayOf("Todos los años", "2024", "2025", "2026")

        binding.spinnerMonth.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        binding.spinnerYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        val filterListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val month = binding.spinnerMonth.selectedItemPosition
                val yearStr = binding.spinnerYear.selectedItem.toString()
                val year = if (yearStr == "Todos los años") 0 else yearStr.toInt()
                viewModel.filterByDate(month, year)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerMonth.onItemSelectedListener = filterListener
        binding.spinnerYear.onItemSelectedListener = filterListener
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myEventosState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            adapter.submitList(state.data)
                            binding.tvEmpty.isVisible = state.data.isEmpty()
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
