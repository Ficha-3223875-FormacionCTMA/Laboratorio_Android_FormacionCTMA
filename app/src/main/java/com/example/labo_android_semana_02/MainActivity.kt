package com.example.labo_android_semana_02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.labo_android_semana_02.data.local.datastore.DataStorePreferenciasRepository
import com.example.labo_android_semana_02.data.local.db.AppDatabase
import com.example.labo_android_semana_02.data.remote.RemoteActividadDataSource
import com.example.labo_android_semana_02.data.remote.SessionTokenProvider
import com.example.labo_android_semana_02.data.remote.api.ActividadApi
import com.example.labo_android_semana_02.data.repository.OfflineActividadRepository
import com.example.labo_android_semana_02.data.repository.RoomReporteRepository
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.domain.repository.PreferenciasRepository
import com.example.labo_android_semana_02.domain.repository.ReporteRepository
import com.example.labo_android_semana_02.ui.*
import com.example.labo_android_semana_02.ui.screens.DetalleActividad
import com.example.labo_android_semana_02.ui.screens.FormularioActividad
import com.example.labo_android_semana_02.ui.screens.PantallaActividades
import com.example.labo_android_semana_02.ui.screens.PantallaActividadesRemotas
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var reporteRepository: RoomReporteRepository
    private lateinit var actividadRepository: OfflineActividadRepository
    private lateinit var preferenciasRepository: PreferenciasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Base de Datos
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reportes_db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
        
        // Red (Retrofit + OkHttp)
        val json = Json { ignoreUnknownKeys = true }
        val loggingInterceptor = HttpLoggingInterceptor().apply { 
            level = HttpLoggingInterceptor.Level.BODY 
            redactHeader("Authorization") // Prohibido loguear el token
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl("https://TU-MOCK-SERVER.mock.pstmn.io/") // URL Simulada
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        
        val api = retrofit.create(ActividadApi::class.java)
        val remoteDataSource = RemoteActividadDataSource(api, SessionTokenProvider())
        
        // Repositorios
        reporteRepository = RoomReporteRepository(db.reporteDao())
        actividadRepository = OfflineActividadRepository(db.actividadDao(), remoteDataSource)
        preferenciasRepository = DataStorePreferenciasRepository(applicationContext)

        setContent {
            Labo_android_semana_02Theme {
                MainApp(reporteRepository, actividadRepository, preferenciasRepository)
            }
        }
    }
}

private data class Item(val titulo: String, val descripcion: String)

enum class AgileTab(val title: String) {
    ACTIVIDADES("Reportes"),
    REMOTO("Sincro"),
    MANIFESTO("Manifiesto"),
    SCRUM("Scrum"),
    TESTING("Pruebas")
}

@Composable
fun MainApp(
    reporteRepo: ReporteRepository,
    actividadRepo: com.example.labo_android_semana_02.domain.repository.ActividadRepository,
    preferencias: PreferenciasRepository
) {
    val navController = rememberNavController()
    val viewModel: ReporteViewModel = viewModel(
        factory = ReporteViewModel.Factory(reporteRepo as RoomReporteRepository, preferencias)
    )
    val syncViewModel: ActividadViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ActividadViewModel(actividadRepo) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val syncUiState by syncViewModel.uiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacionState.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()

    var reporteAEditar by remember { mutableStateOf<Reporte?>(null) }

    NavHost(navController = navController, startDestination = "activities") {
        composable("activities") {
            PantallaPrincipal(
                uiState = uiState,
                syncUiState = syncUiState,
                busqueda = busqueda,
                onBusquedaChange = { viewModel.onBusquedaChange(it) },
                onReporteClick = { reporte -> navController.navigate("activities/${reporte.id}") },
                onAddClick = { 
                    reporteAEditar = null
                    navController.navigate("activities/form") 
                },
                onDelete = { viewModel.eliminarReporte(it) },
                onEdit = { reporte ->
                    reporteAEditar = reporte
                    navController.navigate("activities/form")
                },
                onToggle = { reporte ->
                    viewModel.guardarReporte(reporte.copy(resuelto = !reporte.resuelto))
                },
                onRetry = { /* Reintento de carga si fuera necesario */ },
                onRefresh = { syncViewModel.refresh() }
            )
        }
        composable("activities/form") {
            FormularioScreen(
                reporteAEditar = reporteAEditar,
                operacionState = operacionState,
                onGuardar = { nuevoReporte ->
                    viewModel.guardarReporte(nuevoReporte)
                },
                onExito = {
                    viewModel.resetOperacion()
                    navController.popBackStack()
                },
                onBack = { 
                    reporteAEditar = null
                    navController.popBackStack() 
                }
            )
        }
        composable("activities/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            // El detalle también podría ser reactivo observando el id desde el VM
            val reporteState by viewModel.uiState.collectAsStateWithLifecycle()
            val reporte = (reporteState as? ListadoUiState.Contenido)?.reportes?.find { it.id == id }
            
            // Adaptador para DetalleActividad (que usa ActividadFormativa, pero lo adaptamos)
            // Para este lab, asumo que DetalleActividad puede adaptarse o creamos DetalleReporte
            DetalleReporteContent(
                reporte = reporte,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PantallaPrincipal(
    uiState: ListadoUiState,
    syncUiState: ActividadesUiState,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onReporteClick: (Reporte) -> Unit,
    onAddClick: () -> Unit,
    onDelete: (Reporte) -> Unit,
    onEdit: (Reporte) -> Unit,
    onToggle: (Reporte) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AgileTab.ACTIVIDADES) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AgileTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { 
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            )
                        },
                        label = { 
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                AgileTab.ACTIVIDADES -> PantallaActividades(
                    uiState = uiState,
                    busqueda = busqueda,
                    onBusquedaChange = onBusquedaChange,
                    onReporteClick = onReporteClick,
                    onDeleteReporte = onDelete,
                    onEditReporte = onEdit,
                    onToggleStatus = onToggle,
                    onAddClick = onAddClick,
                    onRetry = onRetry
                )
                AgileTab.REMOTO -> PantallaActividadesRemotas(
                    uiState = syncUiState,
                    onRefresh = onRefresh,
                    onActividadClick = { act -> /* Navegación si aplica */ }
                )
                AgileTab.MANIFESTO -> SeccionManifiesto()
                AgileTab.SCRUM -> SeccionScrum()
                AgileTab.TESTING -> SeccionPruebas()
            }
        }
    }
}

@Composable
fun FormularioScreen(
    reporteAEditar: Reporte?,
    operacionState: OperacionUiState,
    onGuardar: (Reporte) -> Unit,
    onExito: () -> Unit,
    onBack: () -> Unit
) {
    // Elevación de estado para el borrador
    var titulo by rememberSaveable { mutableStateOf(reporteAEditar?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(reporteAEditar?.descripcion ?: "") }
    
    val formatDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaInicial = reporteAEditar?.let { formatDisplay.format(Date(it.fecha)) } ?: ""
    
    var fecha by rememberSaveable { mutableStateOf(fechaInicial) }
    var progreso by rememberSaveable { mutableStateOf(if(reporteAEditar?.resuelto == true) "100" else "0") }

    val guardando = operacionState is OperacionUiState.Cargando
    
    LaunchedEffect(operacionState) {
        if (operacionState is OperacionUiState.Exito) {
            onExito()
        }
    }

    val uiState = buildUiState(titulo, descripcion, fecha, Prioridad.MEDIA, progreso, guardando)

    FormularioActividad(
        uiState = uiState,
        onTituloChange = { titulo = it },
        onDescripcionChange = { descripcion = it },
        onFechaChange = { fecha = it },
        onPrioridadChange = { /* Prioridad fija para Reportes */ },
        onProgresoChange = { progreso = it },
        onGuardar = {
            if (uiState.puedeGuardar && !guardando) {
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaLong = try {
                    format.parse(fecha)?.time ?: System.currentTimeMillis()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
                
                val reporte = Reporte(
                    id = reporteAEditar?.id ?: 0,
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fechaLong,
                    categoriaId = 1, // Por defecto
                    resuelto = progreso == "100"
                )
                onGuardar(reporte)
            }
        },
        onBack = onBack
    )
}

@Composable
fun DetalleReporteContent(
    reporte: Reporte?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TextButton(onClick = onBack) { Text("< Volver") }
        }
    ) { p ->
        if (reporte == null) {
            Box(Modifier.padding(p).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Reporte no encontrado")
            }
        } else {
            Column(Modifier.padding(p).padding(16.dp)) {
                Text(reporte.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(reporte.descripcion)
                Spacer(Modifier.height(16.dp))
                Text("Estado: ${if(reporte.resuelto) "Resuelto" else "Pendiente"}")
            }
        }
    }
}

/**
 * Lógica de validación (Reutilizada y ajustada para Reporte)
 */
fun buildUiState(
    titulo: String,
    descripcion: String,
    fecha: String,
    prioridad: Prioridad,
    progreso: String,
    guardando: Boolean
): FormularioActividadUiState {
    val errores = mutableMapOf<String, String>()
    
    if (titulo.isBlank() || titulo.length < 3) errores["titulo"] = "Título inválido (min 3)"
    if (titulo.length > 80) errores["titulo"] = "Máximo 80 caracteres"
    if (descripcion.length > 240) errores["descripcion"] = "Máximo 240 caracteres"
    
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }
    try {
        if (fecha.isNotBlank()) {
            val date = format.parse(fecha)
            val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
            if (date != null && date.before(cal.time)) errores["fecha"] = "No puede ser anterior a hoy"
        } else {
            errores["fecha"] = "Fecha obligatoria"
        }
    } catch (e: Exception) {
        errores["fecha"] = "Formato dd/mm/aaaa"
    }

    val progInt = progreso.toIntOrNull()
    if (progInt == null || progInt !in 0..100) errores["progreso"] = "Rango 0-100"

    return FormularioActividadUiState(
        titulo = titulo,
        descripcion = descripcion,
        fecha = fecha,
        prioridad = prioridad,
        progreso = progreso,
        errores = errores,
        puedeGuardar = errores.isEmpty(),
        guardando = guardando
    )
}

// --- Secciones teóricas ---

@Composable
private fun SeccionManifiesto() {
    val valores = listOf(
        "Individuos e interacciones sobre procesos y herramientas.",
        "Software funcionando sobre documentación exhaustiva.",
        "Colaboración con el cliente sobre negociación de contratos.",
        "Respuesta ante el cambio sobre seguir un plan estricto."
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Manifiesto Ágil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(valores) { BulletCard(it) }
    }
}

@Composable
private fun SeccionScrum() {
    val roles = listOf(
        Item("Product Owner", "Es la voz del cliente."),
        Item("Scrum Master", "Facilitador del proceso."),
        Item("Developers", "Equipo de construcción.")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Scrum Framework", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(roles) { ItemCard(it) }
    }
}

@Composable
private fun SeccionPruebas() {
    val pruebas = listOf(
        Item("Unitarias", "Componentes aislados."),
        Item("Sistema", "Funcionamiento global.")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Testing", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(pruebas) { ItemCard(it) }
    }
}

@Composable
private fun BulletCard(texto: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ItemCard(item: Item) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = item.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
