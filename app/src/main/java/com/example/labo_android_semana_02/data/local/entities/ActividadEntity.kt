package com.example.labo_android_semana_02.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.labo_android_semana_02.domain.Prioridad

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val progreso: Int,
    val fechaEntrega: Long,
    val prioridad: Prioridad,
    val resuelto: Boolean = false
)
