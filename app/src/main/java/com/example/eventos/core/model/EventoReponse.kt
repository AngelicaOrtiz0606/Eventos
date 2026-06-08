package com.example.eventos.core.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.createTypedArrayList(FechaEvento.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(titulo)
        parcel.writeString(tipo)
        parcel.writeString(descripcion)
        parcel.writeString(imagenUrl)
        parcel.writeString(linkMasInfo)
        parcel.writeString(fechaLimiteInscripcion)
        parcel.writeTypedList(fechasEvento)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Evento> {
        override fun createFromParcel(parcel: Parcel): Evento = Evento(parcel)
        override fun newArray(size: Int): Array<Evento?> = arrayOfNulls(size)
    }
}

data class FechaEvento(
    @SerializedName("fecha")
    val fecha: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("horarios")
    val horarios: List<Horario>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Horario.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fecha)
        parcel.writeString(descripcion)
        parcel.writeTypedList(horarios)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FechaEvento> {
        override fun createFromParcel(parcel: Parcel): FechaEvento = FechaEvento(parcel)
        override fun newArray(size: Int): Array<FechaEvento?> = arrayOfNulls(size)
    }
}

data class Horario(
    @SerializedName("hora_inicio")
    val horaInicio: String,

    @SerializedName("hora_fin")
    val horaFin: String,

    @SerializedName("notas")
    val notas: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(horaInicio)
        parcel.writeString(horaFin)
        parcel.writeString(notas)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Horario> {
        override fun createFromParcel(parcel: Parcel): Horario = Horario(parcel)
        override fun newArray(size: Int): Array<Horario?> = arrayOfNulls(size)
    }
}
