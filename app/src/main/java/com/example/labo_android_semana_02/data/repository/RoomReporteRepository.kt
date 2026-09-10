package com.example.labo_android_semana_02.data.repository

import com.example.labo_android_semana_02.data.local.dao.ReporteDao
import com.example.labo_android_semana_02.data.mapper.toDomain
import com.example.labo_android_semana_02.data.mapper.toEntity
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.domain.repository.ReporteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomReporteRepository(private val reporteDao: ReporteDao) : ReporteRepository {

    override fun getReportes(): Flow<List<Reporte>> = reporteDao.getAllReportes().map { list ->
        list.map { it.toDomain() }
    }

    override fun getReporteById(id: Int): Flow<Reporte?> = reporteDao.getReporteById(id).map { 
        it?.toDomain() 
    }

    override suspend fun insertReporte(reporte: Reporte) {
        reporteDao.insertReporte(reporte.toEntity())
    }

    override suspend fun updateReporte(reporte: Reporte) {
        reporteDao.updateReporte(reporte.toEntity())
    }

    override suspend fun deleteReporte(reporte: Reporte) {
        reporteDao.deleteReporte(reporte.toEntity())
    }
}
