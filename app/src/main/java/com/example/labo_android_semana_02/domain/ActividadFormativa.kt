package com.example.labo_android_semana_02.domain

enum class Prioridad {
    ALTA, MEDIA, BAJA
}

data class ActividadFormativa(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val progreso: Int,
    val fechaEntrega: Long, // Timestamp en milisegundos
    val prioridad: Prioridad = Prioridad.MEDIA
)
