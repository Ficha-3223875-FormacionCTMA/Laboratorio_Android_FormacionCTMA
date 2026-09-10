package com.example.labo_android_semana_02.domain.repository

import com.example.labo_android_semana_02.domain.Reporte
import kotlinx.coroutines.flow.Flow

interface ReporteRepository {
    fun getReportes(): Flow<List<Reporte>>
    fun getReporteById(id: Int): Flow<Reporte?>
    suspend fun insertReporte(reporte: Reporte)
    suspend fun updateReporte(reporte: Reporte)
    suspend fun deleteReporte(reporte: Reporte)
}
