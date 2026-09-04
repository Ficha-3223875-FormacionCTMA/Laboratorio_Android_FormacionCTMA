package com.example.labo_android_semana_02.data.local.dao

import androidx.room.*
import com.example.labo_android_semana_02.data.local.entities.CategoriaEntity
import com.example.labo_android_semana_02.data.local.entities.ReporteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteDao {
    @Query("SELECT * FROM reportes ORDER BY fecha DESC")
    fun getAllReportes(): Flow<List<ReporteEntity>>

    @Query("SELECT * FROM reportes WHERE id = :id")
    fun getReporteById(id: Int): Flow<ReporteEntity?>

    @Query("SELECT * FROM reportes WHERE categoriaId = :categoriaId")
    fun getReportesByCategoria(categoriaId: Int): Flow<List<ReporteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReporte(reporte: ReporteEntity)

    @Update
    fun updateReporte(reporte: ReporteEntity)

    @Delete
    fun deleteReporte(reporte: ReporteEntity)

    // Categorías
    @Query("SELECT * FROM categorias")
    fun getAllCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategoria(categoria: CategoriaEntity)
}
