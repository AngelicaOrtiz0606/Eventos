package com.example.eventos.core.network
import com.example.eventos.core.model.EventoResponse
import retrofit2.http.GET


interface EventoAPI {
    @GET("e395ff3bd9f04e62afd40aa13e600427") /*Es el host de la api*/
    suspend fun getEventos(
    /*Si se tiene API se ponen los query*/
    ): EventoResponse

    /*Acá se pueden poner los otros métodos como POST, PUT, DELETE, etc.*/

}/*Fin interface*/