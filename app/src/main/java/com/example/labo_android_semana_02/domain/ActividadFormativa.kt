package com.example.labo_android_semana_02.domain

data class ActividadFormativa(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val progreso: Int,
    val fechaEntrega: Long // Timestamp en milisegundos
)
