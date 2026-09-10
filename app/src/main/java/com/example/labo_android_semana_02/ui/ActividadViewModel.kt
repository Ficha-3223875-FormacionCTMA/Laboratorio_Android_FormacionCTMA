package com.example.labo_android_semana_02.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.repository.ActividadRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed interface ActividadesUiState {
    data object Cargando : ActividadesUiState
    data class Exito(
        val actividades: List<ActividadFormativa>,
        val mensajeActualizacion: String? = null,
        val esError: Boolean = false
    ) : ActividadesUiState
    data class Error(val mensaje: String) : ActividadesUiState
}

class ActividadViewModel(
    private val repository: ActividadRepository
) : ViewModel() {

    private val _refreshState = MutableStateFlow<String?>(null)
    private val _errorRefresh = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ActividadesUiState> = repository.getActividades()
        .combine(_refreshState) { lista, refresh -> Pair(lista, refresh) }
        .combine(_errorRefresh) { (lista, refresh), error -> Triple(lista, refresh, error) }
        .map { (lista, refresh, error) ->
            if (lista.isEmpty() && error != null) {
                ActividadesUiState.Error(error)
            } else if (lista.isEmpty() && refresh == null) {
                ActividadesUiState.Cargando
            } else {
                ActividadesUiState.Exito(
                    actividades = lista,
                    mensajeActualizacion = error ?: refresh,
                    esError = error != null
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActividadesUiState.Cargando
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _errorRefresh.value = null
            val result = repository.refresh()
            if (result.isSuccess) {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                _refreshState.value = "Actualizado a las ${sdf.format(Date())}"
            } else {
                val error = result.exceptionOrNull()
                if (error is CancellationException) throw error
                _errorRefresh.value = error?.message ?: "Error al actualizar"
            }
        }
    }
}
