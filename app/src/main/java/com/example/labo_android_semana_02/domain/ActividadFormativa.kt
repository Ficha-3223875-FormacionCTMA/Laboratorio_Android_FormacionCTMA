package com.example.labo_android_semana_02.domain

enum class Prioridad {
    ALTA, MEDIA, BAJA
}

data class ActividadFormativa(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad
)