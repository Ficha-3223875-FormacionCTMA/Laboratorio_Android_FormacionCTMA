package com.example.labo_android_semana_02.data.repository

import com.example.labo_android_semana_02.data.local.dao.ActividadDao
import com.example.labo_android_semana_02.data.mapper.toDomain
import com.example.labo_android_semana_02.data.mapper.toEntity
import com.example.labo_android_semana_02.data.remote.RemoteActividadDataSource
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.repository.ActividadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineActividadRepository(
    private val actividadDao: ActividadDao,
    private val remoteDataSource: RemoteActividadDataSource
) : ActividadRepository {

    override fun getActividades(): Flow<List<ActividadFormativa>> {
        return actividadDao.getAllActividades().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refresh(): Result<Unit> {
        val result = remoteDataSource.fetchActividades()
        return if (result.isSuccess) {
            val dtos = result.getOrNull() ?: emptyList()
            // Guardar en Room de forma atómica
            actividadDao.refreshActividades(dtos.map { it.toEntity() })
            Result.success(Unit)
        } else {
            // El error remoto no reemplaza el caché por una lista vacía
            Result.failure(result.exceptionOrNull() ?: Exception("Error desconocido"))
        }
    }
}
