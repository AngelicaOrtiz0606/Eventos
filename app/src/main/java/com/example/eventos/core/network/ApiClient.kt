package com.example.eventos.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://e395ff3bd9f04e62afd40aa13e600427"

    // 1. Agregamos los paréntesis () para crear la instancia
    // 2. Configuramos el nivel a BODY para ver el contenido json
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 3. Creamos el cliente de OkHttp y le pasamos el interceptor para ver el contenido
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // 4. Configuramos Retrofit usando ese cliente
    val EventoApi: EventoAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventoAPI::class.java)
    }

}