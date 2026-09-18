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
) {
    /**
     * HU-19 (CA-19.3): el formulario se considera vacío cuando ninguna de las entradas
     * de texto tiene contenido. Se usa para deshabilitar el botón "Limpiar".
     */
    val estaVacio: Boolean
        get() = titulo.isBlank() && descripcion.isBlank() && fecha.isBlank()
}
