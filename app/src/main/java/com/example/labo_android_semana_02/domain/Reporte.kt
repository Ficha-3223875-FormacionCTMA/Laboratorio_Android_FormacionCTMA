package com.example.labo_android_semana_02.domain

data class Reporte(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val categoriaId: Int,
    val resuelto: Boolean = false, // Nuevo campo para v2
    // HU-21: bandera de "destacado". Vive únicamente en el UiState (no se persiste en Room),
    // por eso se declara con valor por defecto y los mappers no la tocan.
    val esFavorito: Boolean = false,
    val evidenciaFotoUri: String? = null,
    val evidenciaArchivoUri: String? = null
)
