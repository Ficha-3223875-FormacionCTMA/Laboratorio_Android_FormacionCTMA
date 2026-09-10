package com.example.labo_android_semana_02.domain.repository

import com.example.labo_android_semana_02.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow

interface ActividadRepository {
    fun getActividades(): Flow<List<ActividadFormativa>>
    suspend fun refresh(): Result<Unit>
}
