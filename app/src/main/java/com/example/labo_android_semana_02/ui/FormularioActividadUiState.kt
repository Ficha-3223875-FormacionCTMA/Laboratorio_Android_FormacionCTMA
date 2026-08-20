package com.example.labo_android_semana_02.ui

import com.example.labo_android_semana_02.domain.Prioridad

data class FormularioActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "", // Formato dd/MM/yyyy
    val prioridad: Prioridad = Prioridad.MEDIA,
    val progreso: String = "0",
    val errores: Map<String, String> = emptyMap(),
    val puedeGuardar: Boolean = false,
    val guardando: Boolean = false
)
