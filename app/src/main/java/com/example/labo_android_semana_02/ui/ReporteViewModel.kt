package com.example.labo_android_semana_02.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labo_android_semana_02.data.local.datastore.PreferenciasRepository
import com.example.labo_android_semana_02.data.repository.RoomReporteRepository
import com.example.labo_android_semana_02.domain.Reporte
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReporteViewModel(
    private val repository: RoomReporteRepository,
    private val preferencias: PreferenciasRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _operacionState = MutableStateFlow<OperacionUiState>(OperacionUiState.Ideal)
    val operacionState = _operacionState.asStateFlow()

    val uiState: StateFlow<ListadoUiState> = combine(
        repository.getReportes(),
        preferencias.ordenFiltro,
        _busqueda
    ) { reportes, orden, query ->
        Triple(reportes, orden, query)
    }.mapLatest { (reportes, orden, query) ->
        val filtrados = reportes.filter { 
            it.titulo.contains(query, ignoreCase = true) || 
            it.descripcion.contains(query, ignoreCase = true)
        }
        
        val ordenados = when (orden) {
            "FECHA_ASC" -> filtrados.sortedBy { it.fecha }
            else -> filtrados.sortedByDescending { it.fecha }
        }

        if (ordenados.isEmpty()) ListadoUiState.Vacio
        else ListadoUiState.Contenido(ordenados)
    }.catch { e ->
        emit(ListadoUiState.Error(e.message ?: "Error desconocido"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ListadoUiState.Cargando
    )

    fun onBusquedaChange(query: String) {
        _busqueda.value = query
    }

    fun guardarReporte(reporte: Reporte) {
        viewModelScope.launch {
            _operacionState.value = OperacionUiState.Cargando
            try {
                repository.insertReporte(reporte)
                _operacionState.value = OperacionUiState.Exito
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacionState.value = OperacionUiState.Error(e.message ?: "Fallo al guardar")
            }
        }
    }

    fun eliminarReporte(reporte: Reporte) {
        viewModelScope.launch {
            try {
                repository.deleteReporte(reporte)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Manejo silencioso o vía snackbar
            }
        }
    }

    fun actualizarOrden(nuevoOrden: String) {
        viewModelScope.launch {
            preferencias.guardarOrdenFiltro(nuevoOrden)
        }
    }

    fun resetOperacion() {
        _operacionState.value = OperacionUiState.Ideal
    }

    class Factory(
        private val repository: RoomReporteRepository,
        private val preferencias: PreferenciasRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReporteViewModel(repository, preferencias) as T
        }
    }
}
