package com.example.labo_android_semana_02.data.remote.api

import com.example.labo_android_semana_02.data.remote.dto.ActividadDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ActividadApi {
    @GET("api/actividades")
    suspend fun getActividades(
        @Header("Authorization") token: String
    ): Response<List<ActividadDto>>
}
