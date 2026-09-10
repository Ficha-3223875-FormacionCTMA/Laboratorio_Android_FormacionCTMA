package com.example.labo_android_semana_02.ui

import com.example.labo_android_semana_02.domain.Reporte

sealed interface ListadoUiState {
    data object Cargando : ListadoUiState
    data class Contenido(val reportes: List<Reporte>) : ListadoUiState
    data object Vacio : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

sealed interface OperacionUiState {
    data object Ideal : OperacionUiState
    data object Cargando : OperacionUiState
    data object Exito : OperacionUiState
    data class Error(val mensaje: String) : OperacionUiState
}
