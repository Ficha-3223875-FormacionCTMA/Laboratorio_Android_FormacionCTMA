package com.example.labo_android_semana_02

import com.example.labo_android_semana_02.data.local.datastore.PreferenciasRepository
import com.example.labo_android_semana_02.data.repository.RoomReporteRepository
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.ui.ListadoUiState
import com.example.labo_android_semana_02.ui.ReporteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class ReporteViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ReporteViewModel
    
    // Fakes simples
    private val fakeReportes = MutableStateFlow<List<Reporte>>(emptyList())
    private val fakeOrden = MutableStateFlow("FECHA_DESC")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mocking manual/fakes para evitar dependencias pesadas
        val repo = mock(RoomReporteRepository::class.java)
        `when`(repo.getReportes()).thenReturn(fakeReportes)
        
        val prefs = mock(PreferenciasRepository::class.java)
        `when`(prefs.ordenFiltro).thenReturn(fakeOrden)

        viewModel = ReporteViewModel(repo, prefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test_CA_14_1_Cargando_a_Vacio`() = runTest {
        // Al inicio con lista vacía, debe emitir Vacio (tras el estado inicial Cargando)
        val state = viewModel.uiState.first()
        assertTrue(state is ListadoUiState.Vacio)
    }

    @Test
    fun `test_CA_14_2_Insertar_cambia_Contenido`() = runTest {
        val reporte = Reporte(id = 1, titulo = "Test", descripcion = "", fecha = 0, categoriaId = 1)
        fakeReportes.value = listOf(reporte)
        
        val state = viewModel.uiState.first()
        assertTrue(state is ListadoUiState.Contenido)
    }
}
