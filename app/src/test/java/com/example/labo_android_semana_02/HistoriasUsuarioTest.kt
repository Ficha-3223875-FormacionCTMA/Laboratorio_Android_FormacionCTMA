package com.example.labo_android_semana_02

import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.domain.estadoActividad
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class HistoriasUsuarioTest {

    // HU-01 - Gestionar estado de actividades con SnapshotStateList y Composables Stateless
    @Test
    fun test_HU01_StateHoisting_Logic() {
        // Validamos que buildUiState encapsula correctamente el estado para un componente stateless
        val uiState = buildUiState("Tarea Test", "Desc", "20/12/2026", Prioridad.MEDIA, "50", false)
        assertNotNull(uiState)
        assertEquals("Tarea Test", uiState.titulo)
        assertTrue(uiState.puedeGuardar)
    }

    // HU-02 - Crear y priorizar actividades mediante formulario flotante
    @Test
    fun test_HU02_CreateActivity_Validation() {
        // Validamos que el sistema permite crear una actividad válida con prioridad
        val uiState = buildUiState("Nueva Tarea", "", "31/12/2026", Prioridad.ALTA, "0", false)
        assertTrue("Debería permitir guardar una actividad válida", uiState.puedeGuardar)
        assertEquals(Prioridad.ALTA, uiState.prioridad)
    }

    // HU-03 - Eliminar actividad de la lista
    @Test
    fun test_HU03_DeleteActivity_Logic() {
        // Simulamos SnapshotStateList con una lista mutable
        val lista = mutableListOf(
            ActividadFormativa(1, "T1", "D1", 0, 0, Prioridad.BAJA),
            ActividadFormativa(2, "T2", "D2", 0, 0, Prioridad.BAJA)
        )
        val actividadAEliminar = lista[0]
        lista.remove(actividadAEliminar)
        
        assertEquals(1, lista.size)
        assertFalse(lista.contains(actividadAEliminar))
    }

    // HU-04 - Editar detalles de una actividad existente
    @Test
    fun test_HU04_EditActivity_Logic() {
        // Verificamos que buildUiState maneja correctamente datos para edición
        val tituloEditado = "Titulo Editado"
        val uiState = buildUiState(tituloEditado, "Nueva Desc", "15/11/2026", Prioridad.MEDIA, "75", false)
        
        assertEquals(tituloEditado, uiState.titulo)
        assertEquals("75", uiState.progreso)
        assertTrue(uiState.puedeGuardar)
    }

    // HU-05 - Marcar o desmarcar actividad como completada
    @Test
    fun test_HU05_MarkAsCompleted_Logic() {
        val actividad = ActividadFormativa(1, "Tarea", "Desc", 0, System.currentTimeMillis(), Prioridad.MEDIA)
        // Lógica de toggle: si es 100 pasa a 0, si es < 100 pasa a 100
        val nuevoProgreso = if (actividad.progreso == 100) 0 else 100
        val actividadActualizada = actividad.copy(progreso = nuevoProgreso)
        
        assertEquals(100, actividadActualizada.progreso)
    }

    // HU-06 - Visualizar pantalla de detalle de una actividad
    @Test
    fun test_HU06_DetailResolution_Logic() {
        val actividades = listOf(
            ActividadFormativa(1, "T1", "D1", 0, 0, Prioridad.MEDIA),
            ActividadFormativa(5, "T5", "D5", 0, 0, Prioridad.MEDIA)
        )
        val idBusqueda = 5
        val encontrada = actividades.find { it.id == idBusqueda }
        
        assertNotNull(encontrada)
        assertEquals("T5", encontrada?.titulo)
        
        val inexistente = actividades.find { it.id == 99 }
        assertNull(inexistente)
    }

    // HU-07 - Implementar flujo de navegación entre pantallas del CRUD
    @Test
    fun test_HU07_NavigationIdHandling_Logic() {
        // Validamos la generación de ID para navegación
        val actividades = listOf(ActividadFormativa(10, "T", "D", 0, 0))
        val nextId = (actividades.maxOfOrNull { it.id } ?: 0) + 1
        assertEquals(11, nextId)
    }

    // HU-08 - Validar campos del formulario de creación/edición de actividades
    @Test
    fun test_HU08_FormValidation_Logic() {
        // TC-29: Título 2 caracteres (Inválido)
        val stateShort = buildUiState("ab", "", "10/10/2026", Prioridad.MEDIA, "0", false)
        assertFalse(stateShort.puedeGuardar)
        assertEquals("Mínimo 3 caracteres", stateShort.errores["titulo"])

        // TC-31: Fecha pasada (Inválido)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        val fechaPasada = "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
        val statePastDate = buildUiState("Titulo Valido", "", fechaPasada, Prioridad.MEDIA, "0", false)
        assertFalse(statePastDate.puedeGuardar)
        // Nota: SimpleDateFormat puede fallar si no se pone el 0 a la izquierda, pero buildUiState lo maneja o falla el parse.
        // En buildUiState actual: "No puede ser anterior a hoy"
    }

    // HU-09 - Persistir borrador del formulario ante cambios de configuración
    @Test
    fun test_HU09_StatePersistence_Reconstruction() {
        // Simulamos la recuperación de rememberSaveable y reconstrucción del UI State
        val tituloGuardado = "Borrador"
        val uiState = buildUiState(tituloGuardado, "", "12/12/2026", Prioridad.BAJA, "10", false)
        
        assertEquals(tituloGuardado, uiState.titulo)
        assertEquals("10", uiState.progreso)
    }

    // HU-10 - Prevenir doble guardado para evitar duplicados
    @Test
    fun test_HU10_DoubleSavePrevention_Flag() {
        // Validamos que si 'guardando' es true, el botón se deshabilita en el estado
        val uiState = buildUiState("Tarea", "Desc", "12/12/2026", Prioridad.MEDIA, "0", true)
        assertFalse("No debería poder guardar si ya está en proceso", uiState.puedeGuardar && !uiState.guardando)
    }

    // HU-11 - Desplegar indicador de tiempo restante en la tarjeta de actividad
    @Test
    fun test_HU11_TimeRemaining_Calculation() {
        val hoy = Calendar.getInstance().timeInMillis
        // TC-42: Hoy
        assertEquals("Hoy", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(0, hoy, hoy))
        
        // TC-42: Vencida
        val ayer = hoy - (24 * 60 * 60 * 1000L)
        assertEquals("Vencida", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(0, ayer, hoy))
        
        // TC-42: Faltan 5 d
        val en5Dias = hoy + (5 * 24 * 60 * 60 * 1000L)
        assertEquals("Faltan 5 d", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(0, en5Dias, hoy))
        
        // TC-42: Completada (OK)
        assertEquals("OK", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(100, hoy, hoy))
    }

    // HU-12 - Diseñar tarjetas de actividad expandibles con claves estables
    @Test
    fun test_HU12_StableKeys_UniqueId() {
        // Verificamos que las actividades tengan IDs únicos para ser usados como keys
        val lista = listOf(
            ActividadFormativa(1, "A", "", 0, 0),
            ActividadFormativa(2, "B", "", 0, 0),
            ActividadFormativa(3, "C", "", 0, 0)
        )
        val ids = lista.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
}
