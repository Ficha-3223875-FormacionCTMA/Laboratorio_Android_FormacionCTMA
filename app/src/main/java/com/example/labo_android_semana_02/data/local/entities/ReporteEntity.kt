package com.example.labo_android_semana_02.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reportes",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    @ColumnInfo(index = true)
    val categoriaId: Int,
    @ColumnInfo(defaultValue = "0")
    val resuelto: Boolean = false
)
