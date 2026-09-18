package com.example.labo_android_semana_02.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.domain.repository.PreferenciasRepository
import com.example.labo_android_semana_02.domain.repository.ReporteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReporteViewModel(
    private val repository: ReporteRepository,
    private val preferencias: PreferenciasRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _operacionState = MutableStateFlow<OperacionUiState>(OperacionUiState.Ideal)
    val operacionState = _operacionState.asStateFlow()

    /**
     * HU-21: conjunto de IDs marcados como favoritos. Se mantiene en el ViewModel
     * (sobrevive a recomposiciones y rotaciones) y se proyecta sobre el listado
     * antes de emitir el UiState.
     */
    private val _favoritos = MutableStateFlow<Set<Int>>(emptySet())
    val favoritos = _favoritos.asStateFlow()

    private data class EntradaListado(
        val reportes: List<Reporte>,
        val orden: String,
        val query: String,
        val favoritos: Set<Int>
    )

    val uiState: StateFlow<ListadoUiState> = combine(
        repository.getReportes(),
        preferencias.ordenFiltro,
        _busqueda,
        _favoritos
    ) { reportes, orden, query, favoritos ->
        EntradaListado(reportes, orden, query, favoritos)
    }.mapLatest { entrada ->
        val filtrados = entrada.reportes.filter {
            it.titulo.contains(entrada.query, ignoreCase = true) ||
            it.descripcion.contains(entrada.query, ignoreCase = true)
        }

        val ordenados = when (entrada.orden) {
            "FECHA_ASC" -> filtrados.sortedBy { it.fecha }
            else -> filtrados.sortedByDescending { it.fecha }
        }

        // Proyectamos la bandera de favorito sobre cada elemento del listado
        val conFavoritos = ordenados.map { it.copy(esFavorito = entrada.favoritos.contains(it.id)) }

        if (conFavoritos.isEmpty()) ListadoUiState.Vacio
        else ListadoUiState.Contenido(conFavoritos)
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

    /**
     * HU-21 (CA-21.2): alterna de forma reactiva la bandera `esFavorito` del reporte.
     */
    fun alternarFavorito(reporte: Reporte) {
        _favoritos.update { actuales ->
            if (actuales.contains(reporte.id)) actuales - reporte.id
            else actuales + reporte.id
        }
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
                // Si el elemento eliminado estaba destacado, limpiamos su marca
                _favoritos.update { it - reporte.id }
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
        private val repository: ReporteRepository,
        private val preferencias: PreferenciasRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReporteViewModel(repository, preferencias) as T
        }
    }
}
