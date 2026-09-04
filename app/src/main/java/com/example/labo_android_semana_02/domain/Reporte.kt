package com.example.labo_android_semana_02.domain

data class Reporte(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val categoriaId: Int,
    val resuelto: Boolean = false // Nuevo campo para v2
)
