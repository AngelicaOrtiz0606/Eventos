package com.example.eventos.home.eventos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventos.R
import com.example.eventos.core.model.Evento
import com.example.eventos.databinding.ItemEventoBinding

class EventosAdapter(
    private val onItemClicked: (Evento) -> Unit = {}
) : ListAdapter<Evento, EventosAdapter.EventosViewHolder>(DIFF) {


/*Que vista voy a usar*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
        val binding = ItemEventoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventosViewHolder(binding)
    }

/*Va a crear uno nuevo en la posición que necesita*/
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

            Glide.with(binding.ivEvento.context)
                .load(evento.imagenUrl)
                .placeholder(R.drawable.fca)
                .centerCrop()
                .into(binding.ivEvento)

            binding.root.setOnClickListener { onItemClicked(evento) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Evento>() {
            override fun areItemsTheSame(oldItem: Evento, newItem: Evento) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Evento, newItem: Evento) =
                oldItem == newItem
        }
    }
}
