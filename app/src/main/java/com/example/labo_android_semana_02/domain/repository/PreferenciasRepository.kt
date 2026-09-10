package com.example.labo_android_semana_02.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {
    val ordenFiltro: Flow<String>
    suspend fun guardarOrdenFiltro(orden: String)
}
