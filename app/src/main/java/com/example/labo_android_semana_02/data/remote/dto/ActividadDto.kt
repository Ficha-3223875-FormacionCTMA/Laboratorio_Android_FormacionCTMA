package com.example.labo_android_semana_02.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActividadDto(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    @SerialName("fecha_entrega")
    val fechaEntrega: Long,
    val progreso: Int,
    val resuelto: Boolean
)
