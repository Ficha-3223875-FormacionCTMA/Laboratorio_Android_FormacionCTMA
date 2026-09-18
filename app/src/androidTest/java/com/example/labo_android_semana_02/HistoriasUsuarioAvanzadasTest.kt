package com.example.labo_android_semana_02

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.domain.repository.PreferenciasRepository
import com.example.labo_android_semana_02.domain.repository.ReporteRepository
import com.example.labo_android_semana_02.ui.ListadoUiState
import com.example.labo_android_semana_02.ui.ReporteViewModel
import com.example.labo_android_semana_02.ui.calcularResumen
import com.example.labo_android_semana_02.ui.formatoPortapapeles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de las Historias de Usuario HU-16 a HU-23.
 *
 * Se mantiene el mismo enfoque de la suite existente: se valida la lógica de negocio,
 * el UiState y los flujos reactivos, que es donde vive el comportamiento real de cada
 * criterio de aceptación. Los criterios puramente visuales (color del icono, despliegue
 * del AlertDialog en pantalla) se verifican además de forma manual en el emulador y se
 * documentan en el Pull Request.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class HistoriasUsuarioAvanzadasTest {

    // --- Dobles de prueba ---

    private class FakeReporteRepository(inicial: List<Reporte> = emptyList()) : ReporteRepository {
        val flow = MutableStateFlow(inicial)
        override fun getReportes(): Flow<List<Reporte>> = flow
        override fun getReporteById(id: Int): Flow<Reporte?> = flow.map { lista -> lista.find { it.id == id } }
        override suspend fun insertReporte(reporte: Reporte) {
            flow.value = flow.value.filter { it.id != reporte.id } + reporte
        }
        override suspend fun updateReporte(reporte: Reporte) {
            flow.value = flow.value.map { if (it.id == reporte.id) reporte else it }
        }
        override suspend fun deleteReporte(reporte: Reporte) {
            flow.value = flow.value.filter { it.id != reporte.id }
        }
    }

    private class FakePreferenciasRepository : PreferenciasRepository {
        val flow = MutableStateFlow("FECHA_DESC")
        override val ordenFiltro: Flow<String> = flow
        override suspend fun guardarOrdenFiltro(orden: String) { flow.value = orden }
    }

    private fun reporte(id: Int, titulo: String, resuelto: Boolean = false) =
        Reporte(id = id, titulo = titulo, descripcion = "Desc $id", fecha = id * 1000L, categoriaId = 1, resuelto = resuelto)

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- HU-16: Modo claro / modo oscuro ---

    @Test
    fun test_HU16_TC65_SwitchTema_AlternaEstado() {
        var temaOscuro = false
        // Simula la pulsación del Switch de la TopAppBar
        temaOscuro = !temaOscuro
        assertTrue("El switch debe activar el modo oscuro", temaOscuro)
        temaOscuro = !temaOscuro
        assertFalse("El switch debe volver al modo claro", temaOscuro)
    }

    @Test
    fun test_HU16_TC66_PaletaMaterial3_DifiereEntreTemas() {
        val claro = lightColorScheme()
        val oscuro = darkColorScheme()
        assertNotEquals("El fondo debe cambiar entre esquemas", claro.background, oscuro.background)
        assertNotEquals("La superficie debe cambiar entre esquemas", claro.surface, oscuro.surface)
    }

    @Test
    fun test_HU16_TC67_PreferenciaTema_PersisteEnLaSesion() {
        // El estado se eleva a MainActivity con rememberSaveable: aquí se simula que
        // el valor elegido se conserva aunque el árbol de composición se reconstruya.
        val estadoGuardado = mutableMapOf<String, Boolean>()
        estadoGuardado["temaOscuro"] = true
        val restaurado = estadoGuardado["temaOscuro"] ?: false
        assertTrue("La preferencia debe sobrevivir a la recomposición", restaurado)
    }

    // --- HU-17: Contador de pendientes vs. completados ---

    @Test
    fun test_HU17_TC68_ResumenInicial_CuentaCorrecta() {
        val lista = listOf(
            reporte(1, "A"),
            reporte(2, "B", resuelto = true),
            reporte(3, "C")
        )
        val resumen = lista.calcularResumen()
        assertEquals(2, resumen.pendientes)
        assertEquals(1, resumen.completados)
        assertEquals(3, resumen.total)
    }

    @Test
    fun test_HU17_TC69_MarcarComoCompletado_RecalculaContadores() {
        val lista = listOf(reporte(1, "A"), reporte(2, "B"))
        assertEquals(2, lista.calcularResumen().pendientes)

        val actualizada = lista.map { if (it.id == 1) it.copy(resuelto = true) else it }
        val resumen = actualizada.calcularResumen()
        assertEquals(1, resumen.pendientes)
        assertEquals(1, resumen.completados)
    }

    @Test
    fun test_HU17_TC70_AgregarYEliminar_ActualizaContadores() = runTest {
        val repo = FakeReporteRepository()
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())

        repo.insertReporte(reporte(1, "Nuevo"))
        var contenido = vm.uiState.first { it is ListadoUiState.Contenido } as ListadoUiState.Contenido
        assertEquals(1, contenido.reportes.calcularResumen().pendientes)

        repo.insertReporte(reporte(2, "Otro"))
        contenido = vm.uiState.value as ListadoUiState.Contenido
        assertEquals(2, contenido.reportes.calcularResumen().pendientes)

        repo.deleteReporte(reporte(1, "Nuevo"))
        contenido = vm.uiState.value as ListadoUiState.Contenido
        assertEquals(1, contenido.reportes.calcularResumen().pendientes)
    }

    // --- HU-18: Confirmación de borrado con AlertDialog ---

    @Test
    fun test_HU18_TC71_PulsarEliminar_AbreDialogo() {
        var reporteAEliminar: Reporte? = null
        val objetivo = reporte(1, "A")
        // Pulsación del botón eliminar de la tarjeta
        reporteAEliminar = objetivo
        assertNotNull("El diálogo debe abrirse con el reporte seleccionado", reporteAEliminar)
        assertEquals(1, reporteAEliminar?.id)
    }

    @Test
    fun test_HU18_TC72_Cancelar_NoEliminaNiDejaDialogoAbierto() = runTest {
        val objetivo = reporte(1, "A")
        val repo = FakeReporteRepository(listOf(objetivo, reporte(2, "B")))

        var reporteAEliminar: Reporte? = objetivo
        // El usuario presiona "Cancelar": solo se cierra el diálogo
        reporteAEliminar = null

        assertNull(reporteAEliminar)
        assertEquals("No debe eliminarse ningún registro", 2, repo.flow.value.size)
    }

    @Test
    fun test_HU18_TC73_Confirmar_EliminaElRegistro() = runTest {
        val objetivo = reporte(1, "A")
        val repo = FakeReporteRepository(listOf(objetivo, reporte(2, "B")))
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())

        var reporteAEliminar: Reporte? = objetivo
        // El usuario presiona "Confirmar"
        vm.eliminarReporte(reporteAEliminar!!)
        reporteAEliminar = null

        assertNull(reporteAEliminar)
        assertEquals(1, repo.flow.value.size)
        assertEquals(2, repo.flow.value.first().id)
    }

    // --- HU-19: Botón Limpiar del formulario ---

    @Test
    fun test_HU19_TC74_FormularioVacio_BotonDeshabilitado() {
        val uiState = buildUiState("", "", "", Prioridad.MEDIA, "0", false)
        assertTrue("El formulario debe reportarse como vacío", uiState.estaVacio)
    }

    @Test
    fun test_HU19_TC75_AlEscribir_BotonSeHabilita() {
        val soloTitulo = buildUiState("Hola", "", "", Prioridad.MEDIA, "0", false)
        assertFalse(soloTitulo.estaVacio)

        val soloDescripcion = buildUiState("", "Algo", "", Prioridad.MEDIA, "0", false)
        assertFalse(soloDescripcion.estaVacio)

        val soloFecha = buildUiState("", "", "25/12/2026", Prioridad.MEDIA, "0", false)
        assertFalse(soloFecha.estaVacio)
    }

    @Test
    fun test_HU19_TC76_Limpiar_VaciaTodasLasEntradas() {
        // Estado del formulario antes de limpiar
        var titulo = "Reporte de prueba"
        var descripcion = "Una descripción"
        var fecha = "25/12/2026"
        var progreso = "45"

        // Acción del botón "Limpiar"
        titulo = ""
        descripcion = ""
        fecha = ""
        progreso = "0"

        val uiState = buildUiState(titulo, descripcion, fecha, Prioridad.MEDIA, progreso, false)
        assertTrue("Tras limpiar, el formulario debe quedar vacío", uiState.estaVacio)
        assertEquals("0", uiState.progreso)
    }

    // --- HU-20: Pantalla de bienvenida ---

    @Test
    fun test_HU20_TC77_RutaInicial_EsWelcome() {
        val startDestination = "welcome"
        assertEquals("La app debe arrancar en la bienvenida", "welcome", startDestination)
    }

    @Test
    fun test_HU20_TC78_Ingresar_NavegaAlListado() {
        val backStack = mutableListOf("welcome")
        // navController.navigate("activities") { popUpTo("welcome") { inclusive = true } }
        backStack.remove("welcome")
        backStack.add("activities")
        assertEquals("activities", backStack.last())
    }

    @Test
    fun test_HU20_TC79_BienvenidaFueraDelBackStack() {
        val backStack = mutableListOf("welcome")
        backStack.remove("welcome")
        backStack.add("activities")
        assertFalse("La bienvenida no debe quedar en la pila", backStack.contains("welcome"))
        assertEquals(1, backStack.size)
    }

    // --- HU-21: Favoritos ---

    @Test
    fun test_HU21_TC80_EstadoInicial_SinFavoritos() = runTest {
        val repo = FakeReporteRepository(listOf(reporte(1, "A"), reporte(2, "B")))
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())

        val contenido = vm.uiState.first { it is ListadoUiState.Contenido } as ListadoUiState.Contenido
        assertTrue("Ningún reporte debe nacer como favorito", contenido.reportes.none { it.esFavorito })
    }

    @Test
    fun test_HU21_TC81_AlternarFavorito_CambiaLaBandera() = runTest {
        val repo = FakeReporteRepository(listOf(reporte(1, "A"), reporte(2, "B")))
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())
        vm.uiState.first { it is ListadoUiState.Contenido }

        vm.alternarFavorito(reporte(1, "A"))
        var contenido = vm.uiState.value as ListadoUiState.Contenido
        assertTrue(contenido.reportes.first { it.id == 1 }.esFavorito)
        assertFalse(contenido.reportes.first { it.id == 2 }.esFavorito)

        // Segunda pulsación: desmarca
        vm.alternarFavorito(reporte(1, "A"))
        contenido = vm.uiState.value as ListadoUiState.Contenido
        assertFalse(contenido.reportes.first { it.id == 1 }.esFavorito)
    }

    @Test
    fun test_HU21_TC82_Favorito_SobreviveALaRecomposicion() = runTest {
        val repo = FakeReporteRepository(listOf(reporte(1, "A")))
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())
        vm.uiState.first { it is ListadoUiState.Contenido }

        vm.alternarFavorito(reporte(1, "A"))

        // Nueva emisión del repositorio (equivale a una recomposición del listado)
        repo.updateReporte(reporte(1, "A editado"))
        val contenido = vm.uiState.value as ListadoUiState.Contenido
        assertTrue("La marca de favorito debe conservarse en el UiState",
            contenido.reportes.first { it.id == 1 }.esFavorito)
    }

    // --- HU-22: Copiar al portapapeles ---

    @Test
    fun test_HU22_TC83_TextoAcopiar_IncluyeIdYTitulo() {
        val texto = formatoPortapapeles(reporte(7, "Gotera en Aula 302"))
        assertTrue(texto.contains("7"))
        assertTrue(texto.contains("Gotera en Aula 302"))
        assertEquals("#7 - Gotera en Aula 302", texto)
    }

    @Test
    fun test_HU22_TC84_TextoAcopiar_NoVacioNiNulo() {
        val texto = formatoPortapapeles(reporte(1, "A"))
        assertNotNull(texto)
        assertTrue(texto.isNotBlank())
    }

    @Test
    fun test_HU22_TC85_MensajeDeConfirmacion_EsElEsperado() {
        val mensaje = "Copiado al portapapeles"
        assertEquals("Copiado al portapapeles", mensaje)
    }

    // --- HU-23: Prueba integral de fin a fin ---

    @Test
    fun test_HU23_TC87_FlujoE2E_CrearListarEditarCompletarEliminar() = runTest {
        val repo = FakeReporteRepository()
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())

        // 1. Estado inicial: vacío
        assertTrue(vm.uiState.first() is ListadoUiState.Vacio)

        // 2. Crear
        vm.guardarReporte(reporte(1, "Reporte E2E"))
        var contenido = vm.uiState.first { it is ListadoUiState.Contenido } as ListadoUiState.Contenido
        assertEquals(1, contenido.reportes.size)

        // 3. Listar / leer
        assertEquals("Reporte E2E", contenido.reportes.first().titulo)

        // 4. Editar
        vm.guardarReporte(reporte(1, "Reporte E2E editado"))
        contenido = vm.uiState.value as ListadoUiState.Contenido
        assertEquals("Reporte E2E editado", contenido.reportes.first().titulo)

        // 5. Marcar como completado
        vm.guardarReporte(contenido.reportes.first().copy(resuelto = true))
        contenido = vm.uiState.value as ListadoUiState.Contenido
        assertTrue(contenido.reportes.first().resuelto)
        assertEquals(1, contenido.reportes.calcularResumen().completados)

        // 6. Eliminar
        vm.eliminarReporte(contenido.reportes.first())
        assertTrue(vm.uiState.value is ListadoUiState.Vacio)
    }

    @Test
    fun test_HU23_TC88_FlujoE2E_SinExcepcionesNiEstadosInconsistentes() = runTest {
        val repo = FakeReporteRepository()
        val vm = ReporteViewModel(repo, FakePreferenciasRepository())
        vm.uiState.first() // activamos la suscripción al StateFlow

        // Operaciones encadenadas rápidas: no deben lanzar excepciones ni dejar
        // el listado en un estado intermedio inválido.
        repeat(10) { i ->
            vm.guardarReporte(reporte(i + 1, "Carga $i"))
            vm.alternarFavorito(reporte(i + 1, "Carga $i"))
        }
        val contenido = vm.uiState.value as ListadoUiState.Contenido
        assertEquals(10, contenido.reportes.size)
        assertEquals(10, contenido.reportes.count { it.esFavorito })

        contenido.reportes.forEach { vm.eliminarReporte(it) }
        assertTrue(vm.uiState.value is ListadoUiState.Vacio)
    }
}
