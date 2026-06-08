package com.example.eventos.home.eventos

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventos.R
import com.example.eventos.core.model.Evento
import com.example.eventos.core.network.UnsafeOkHttpClient
import com.example.eventos.databinding.ItemEventoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventosAdapter(
    private val onItemClicked: (Evento) -> Unit = {}
) : ListAdapter<Evento, EventosAdapter.EventosViewHolder>(DIFF) {

    private val unsafeClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
    private val adapterScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
        val binding = ItemEventoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventosViewHolder(
        private val binding: ItemEventoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(evento: Evento) {
            binding.tvTitle.text = evento.titulo
            binding.tvDescription.text = evento.descripcion
            binding.tvType.text = evento.tipo.uppercase()

            if (evento.fechasEvento.isNotEmpty()) {
                val fechaInfo = evento.fechasEvento[0]
                binding.tvDate.text = fechaInfo.fecha
                
                if (fechaInfo.horarios.isNotEmpty()) {
                    val horario = fechaInfo.horarios[0]
                    binding.tvTime.text = "${horario.horaInicio} - ${horario.horaFin}"
                    binding.tvTime.visibility = View.VISIBLE
                } else {
                    binding.tvTime.visibility = View.GONE
                }
            } else {
                binding.tvDate.text = "Límite: ${evento.fechaLimiteInscripcion ?: "N/A"}"
                binding.tvTime.visibility = View.GONE
            }

            // Descarga manual de la imagen usando un cliente que ignora errores de SSL
            if (evento.imagenUrl.isNotEmpty()) {
                binding.ivEvento.setImageResource(R.drawable.fca) 
                
                adapterScope.launch {
                    try {
                        val bytes = withContext(Dispatchers.IO) {
                            val request = okhttp3.Request.Builder()
                                .url(evento.imagenUrl)
                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .build()
                            
                            unsafeClient.newCall(request).execute().use { response ->
                                if (!response.isSuccessful) null else response.body?.bytes()
                            }
                        }

                        if (bytes != null) {
                            Glide.with(binding.ivEvento.context)
                                .load(bytes)
                                .placeholder(R.drawable.fca)
                                .error(R.drawable.fca)
                                .centerCrop()
                                .into(binding.ivEvento)
                        }
                    } catch (e: Exception) {
                        Log.e("EventosAdapter", "Error al descargar imagen: ${evento.imagenUrl}", e)
                    }
                }
            } else {
                binding.ivEvento.setImageResource(R.drawable.fca)
            }

            binding.root.setOnClickListener { onItemClicked(evento) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Evento>() {
            override fun areItemsTheSame(oldItem: Evento, newItem: Evento): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Evento, newItem: Evento): Boolean =
                oldItem == newItem
        }
    }
}
