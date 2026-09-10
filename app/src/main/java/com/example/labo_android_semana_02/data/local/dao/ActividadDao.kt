package com.example.labo_android_semana_02.data.local.dao

import androidx.room.*
import com.example.labo_android_semana_02.data.local.entities.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY fechaEntrega ASC")
    fun getAllActividades(): Flow<List<ActividadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActividades(actividades: List<ActividadEntity>)

    @Query("DELETE FROM actividades")
    fun clearActividades()

    @Transaction
    fun refreshActividades(actividades: List<ActividadEntity>) {
        clearActividades()
        insertActividades(actividades)
    }
}
