package com.example.labo_android_semana_02.data.repository

import com.example.labo_android_semana_02.data.local.dao.ReporteDao
import com.example.labo_android_semana_02.data.mapper.toDomain
import com.example.labo_android_semana_02.data.mapper.toEntity
import com.example.labo_android_semana_02.domain.Reporte
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomReporteRepository(private val reporteDao: ReporteDao) {

    fun getReportes(): Flow<List<Reporte>> = reporteDao.getAllReportes().map { list ->
        list.map { it.toDomain() }
    }

    fun getReporteById(id: Int): Flow<Reporte?> = reporteDao.getReporteById(id).map { 
        it?.toDomain() 
    }

    suspend fun insertReporte(reporte: Reporte) {
        reporteDao.insertReporte(reporte.toEntity())
    }

    suspend fun updateReporte(reporte: Reporte) {
        reporteDao.updateReporte(reporte.toEntity())
    }

    suspend fun deleteReporte(reporte: Reporte) {
        reporteDao.deleteReporte(reporte.toEntity())
    }
}
