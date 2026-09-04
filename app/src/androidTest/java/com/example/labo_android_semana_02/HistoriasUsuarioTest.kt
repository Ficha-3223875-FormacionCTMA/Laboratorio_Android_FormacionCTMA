package com.example.labo_android_semana_02

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.labo_android_semana_02.data.local.dao.ReporteDao
import com.example.labo_android_semana_02.data.local.db.AppDatabase
import com.example.labo_android_semana_02.data.local.entities.CategoriaEntity
import com.example.labo_android_semana_02.data.local.entities.ReporteEntity
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.Prioridad
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
class HistoriasUsuarioTest {

    // --- Configuración para Pruebas de Persistencia (HU-13) ---
    private lateinit var db: AppDatabase
    private lateinit var dao: ReporteDao

    private val TEST_DB = "migration-test"

    @get:Rule
    val migrationHelper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.reporteDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    // --- HU-01 a HU-12: Lógica de Negocio y UI State ---

    @Test
    fun test_HU01_StateHoisting_Logic() {
        val uiState = buildUiState("Tarea Test", "Desc", "20/12/2026", Prioridad.MEDIA, "50", false)
        assertNotNull(uiState)
        assertEquals("Tarea Test", uiState.titulo)
        assertTrue(uiState.puedeGuardar)
    }

    @Test
    fun test_HU02_CreateActivity_Validation() {
        val uiState = buildUiState("Nueva Tarea", "", "31/12/2026", Prioridad.ALTA, "0", false)
        assertTrue("Debería permitir guardar una actividad válida", uiState.puedeGuardar)
        assertEquals(Prioridad.ALTA, uiState.prioridad)
    }

    @Test
    fun test_HU03_DeleteActivity_Logic() {
        val lista = mutableListOf(
            ActividadFormativa(1, "T1", "D1", 0, 0, Prioridad.BAJA),
            ActividadFormativa(2, "T2", "D2", 0, 0, Prioridad.BAJA)
        )
        val actividadAEliminar = lista[0]
        lista.remove(actividadAEliminar)
        assertEquals(1, lista.size)
        assertFalse(lista.contains(actividadAEliminar))
    }

    @Test
    fun test_HU04_EditActivity_Logic() {
        val tituloEditado = "Titulo Editado"
        val uiState = buildUiState(tituloEditado, "Nueva Desc", "15/11/2026", Prioridad.MEDIA, "75", false)
        assertEquals(tituloEditado, uiState.titulo)
        assertEquals("75", uiState.progreso)
        assertTrue(uiState.puedeGuardar)
    }

    @Test
    fun test_HU05_MarkAsCompleted_Logic() {
        val actividad = ActividadFormativa(1, "Tarea", "Desc", 0, System.currentTimeMillis(), Prioridad.MEDIA)
        val nuevoProgreso = if (actividad.progreso == 100) 0 else 100
        val actividadActualizada = actividad.copy(progreso = nuevoProgreso)
        assertEquals(100, actividadActualizada.progreso)
    }

    @Test
    fun test_HU06_DetailResolution_Logic() {
        val actividades = listOf(
            ActividadFormativa(1, "T1", "D1", 0, 0, Prioridad.MEDIA),
            ActividadFormativa(5, "T5", "D5", 0, 0, Prioridad.MEDIA)
        )
        val encontrada = actividades.find { it.id == 5 }
        assertNotNull(encontrada)
        assertEquals("T5", encontrada?.titulo)
        
        val inexistente = actividades.find { it.id == 99 }
        assertNull(inexistente)
    }

    @Test
    fun test_HU07_NavigationIdHandling_Logic() {
        val actividades = listOf(ActividadFormativa(10, "T", "D", 0, 0))
        val nextId = (actividades.maxOfOrNull { it.id } ?: 0) + 1
        assertEquals(11, nextId)
    }

    @Test
    fun test_HU08_FormValidation_Logic() {
        val stateShort = buildUiState("ab", "", "10/10/2026", Prioridad.MEDIA, "0", false)
        assertFalse(stateShort.puedeGuardar)
        assertEquals("Mínimo 3 caracteres", stateShort.errores["titulo"])

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -5)
        val fechaPasada = "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
        val statePastDate = buildUiState("Titulo Valido", "", fechaPasada, Prioridad.MEDIA, "0", false)
        assertFalse(statePastDate.puedeGuardar)
    }

    @Test
    fun test_HU09_StatePersistence_Reconstruction() {
        val tituloGuardado = "Borrador"
        val uiState = buildUiState(tituloGuardado, "", "12/12/2026", Prioridad.BAJA, "10", false)
        assertEquals(tituloGuardado, uiState.titulo)
        assertEquals("10", uiState.progreso)
    }

    @Test
    fun test_HU10_DoubleSavePrevention_Flag() {
        val uiState = buildUiState("Tarea", "Desc", "12/12/2026", Prioridad.MEDIA, "0", true)
        assertFalse("No debería poder guardar si ya está en proceso", uiState.puedeGuardar && !uiState.guardando)
    }

    @Test
    fun test_HU11_TimeRemaining_Calculation() {
        val hoy = Calendar.getInstance().timeInMillis
        assertEquals("Hoy", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(0, hoy, hoy))
        
        val ayer = hoy - (24 * 60 * 60 * 1000L)
        assertEquals("Vencida", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(0, ayer, hoy))
        
        val en5Dias = hoy + (5 * 24 * 60 * 60 * 1000L)
        assertEquals("Faltan 5 d", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(0, en5Dias, hoy))
        
        assertEquals("OK", com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(100, hoy, hoy))
    }

    @Test
    fun test_HU12_StableKeys_UniqueId() {
        val lista = listOf(
            ActividadFormativa(1, "A", "", 0, 0),
            ActividadFormativa(2, "B", "", 0, 0),
            ActividadFormativa(3, "C", "", 0, 0)
        )
        val ids = lista.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    // --- HU-13: Persistencia Local (Room & Migration) ---

    @Test
    @Throws(Exception::class)
    fun test_HU13_CA02_InsertAndGetReporte_Reactive() = runBlocking {
        val categoria = CategoriaEntity(id = 1, nombre = "Infraestructura", icono = "🏗️")
        dao.insertCategoria(categoria)

        val reporte = ReporteEntity(
            id = 1,
            titulo = "Gotera en Aula 302",
            descripcion = "Filtración grave",
            fecha = System.currentTimeMillis(),
            categoriaId = 1,
            resuelto = false
        )
        dao.insertReporte(reporte)

        val reportes = dao.getAllReportes().first()
        assertEquals(1, reportes.size)
        assertEquals("Gotera en Aula 302", reportes[0].titulo)
    }

    @Test
    fun test_HU13_CA04_GetInexistentReporte_Controlled() = runBlocking {
        val reporte = dao.getReporteById(999).first()
        assertNull(reporte)
    }

    @Test
    @Throws(IOException::class)
    fun test_HU13_CA03_Migration1To2_DataPersistence() {
        var db = migrationHelper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO categorias (id, nombre, icono) VALUES (1, 'Salud', '🏥')")
            execSQL("INSERT INTO reportes (id, titulo, descripcion, fecha, categoriaId) VALUES (1, 'Reporte v1', 'Desc', 1000, 1)")
            close()
        }

        db = migrationHelper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2)

        val cursor = db.query("SELECT * FROM reportes WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        
        val resueltoColumnIndex = cursor.getColumnIndex("resuelto")
        assertNotEquals(-1, resueltoColumnIndex)
        assertEquals(0, cursor.getInt(resueltoColumnIndex)) // 0 es false en SQLite
        
        assertEquals("Reporte v1", cursor.getString(cursor.getColumnIndex("titulo")))
        cursor.close()
    }
}
