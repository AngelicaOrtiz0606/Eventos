package com.example.eventos.core.model

import com.google.gson.annotations.SerializedName

data class EventoResponse(
    @SerializedName("eventos")
    val eventos: List<Evento>
)

data class Evento(
    @SerializedName("id")
    val id: Int,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("imagen_url")
    val imagenUrl: String,

    @SerializedName("link_mas_info")
    val linkMasInfo: String,

    @SerializedName("fecha_limite_inscripcion")
    val fechaLimiteInscripcion: String?,

    @SerializedName("fechas_evento")
    val fechasEvento: List<FechaEvento>
)

data class FechaEvento(
    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("horarios")
    val horarios: List<Horario>
)

data class Horario(
    @SerializedName("hora_inicio")
    val horaInicio: String,

    @SerializedName("hora_fin")
    val horaFin: String,

    @SerializedName("notas")
    val notas: String
)