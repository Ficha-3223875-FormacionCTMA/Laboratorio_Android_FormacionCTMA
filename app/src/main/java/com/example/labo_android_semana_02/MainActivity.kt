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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.ActividadRepository
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.ui.FormularioActividadUiState
import com.example.labo_android_semana_02.ui.screens.DetalleActividad
import com.example.labo_android_semana_02.ui.screens.FormularioActividad
import com.example.labo_android_semana_02.ui.screens.PantallaActividades
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Labo_android_semana_02Theme {
                MainApp()
            }
        }
    }
}

private data class Item(val titulo: String, val descripcion: String)

enum class AgileTab(val title: String) {
    ACTIVIDADES("Actividades"),
    MANIFESTO("Manifiesto"),
    SCRUM("Scrum"),
    TESTING("Pruebas")
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    // Fuente de verdad reactiva para toda la app
    val actividades = remember { 
        mutableStateListOf<ActividadFormativa>().apply { 
            addAll(ActividadRepository.actividades) 
        } 
    }

    // Estado compartido para edición (elevado a MainApp)
    var actividadAEditar by remember { mutableStateOf<ActividadFormativa?>(null) }

    NavHost(navController = navController, startDestination = "activities") {
        composable("activities") {
            PantallaPrincipal(
                actividades = actividades,
                onActividadClick = { id -> navController.navigate("activities/$id") },
                onAddClick = { 
                    actividadAEditar = null
                    navController.navigate("activities/form") 
                },
                onDelete = { act -> actividades.remove(act) },
                onEdit = { act ->
                    actividadAEditar = act
                    navController.navigate("activities/form")
                },
                onToggle = { act ->
                    val index = actividades.indexOf(act)
                    if (index != -1) {
                        val nuevoProgreso = if (act.progreso == 100) 0 else 100
                        actividades[index] = act.copy(progreso = nuevoProgreso)
                    }
                }
            )
        }
        composable("activities/form") {
            FormularioScreen(
                actividadAEditar = actividadAEditar,
                onGuardar = { nuevaActividad ->
                    val index = actividades.indexOfFirst { it.id == nuevaActividad.id }
                    if (index != -1) {
                        actividades[index] = nuevaActividad
                    } else {
                        actividades.add(nuevaActividad)
                    }
                    actividadAEditar = null
                    navController.popBackStack()
                },
                onBack = { 
                    actividadAEditar = null
                    navController.popBackStack() 
                },
                nextId = (actividades.maxOfOrNull { it.id } ?: 0) + 1
            )
        }
        composable("activities/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val actividad = actividades.find { it.id == id }
            DetalleActividad(
                actividad = actividad,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PantallaPrincipal(
    actividades: List<ActividadFormativa>,
    onActividadClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onDelete: (ActividadFormativa) -> Unit,
    onEdit: (ActividadFormativa) -> Unit,
    onToggle: (ActividadFormativa) -> Unit
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
                    actividades = actividades,
                    onActividadClick = { act -> onActividadClick(act.id) },
                    onDeleteActividad = onDelete,
                    onEditActividad = onEdit,
                    onToggleStatus = onToggle,
                    onAddClick = onAddClick,
                    onReiniciarFiltros = { }
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
    actividadAEditar: ActividadFormativa?,
    onGuardar: (ActividadFormativa) -> Unit,
    onBack: () -> Unit,
    nextId: Int
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Elevación de estado simple para el borrador del formulario
    var titulo by rememberSaveable { mutableStateOf(actividadAEditar?.titulo ?: "") }
    var descripcion by rememberSaveable { mutableStateOf(actividadAEditar?.descripcion ?: "") }
    
    val formatDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaInicial = actividadAEditar?.let { formatDisplay.format(Date(it.fechaEntrega)) } ?: ""
    
    var fecha by rememberSaveable { mutableStateOf(fechaInicial) }
    var prioridad by rememberSaveable { mutableStateOf(actividadAEditar?.prioridad ?: Prioridad.MEDIA) }
    var progreso by rememberSaveable { mutableStateOf(actividadAEditar?.progreso?.toString() ?: "0") }
    var guardando by remember { mutableStateOf(false) }

    // Reconstrucción del UiState para pasarlo al componente stateless
    val uiState = buildUiState(titulo, descripcion, fecha, prioridad, progreso, guardando)

    FormularioActividad(
        uiState = uiState,
        onTituloChange = { titulo = it },
        onDescripcionChange = { descripcion = it },
        onFechaChange = { fecha = it },
        onPrioridadChange = { prioridad = it },
        onProgresoChange = { progreso = it },
        onGuardar = {
            if (uiState.puedeGuardar && !guardando) {
                guardando = true
                coroutineScope.launch {
                    delay(800) // Protección contra doble toque y feedback visual
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaLong = try {
                        format.parse(fecha)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                    
                    val nueva = ActividadFormativa(
                        id = actividadAEditar?.id ?: nextId,
                        titulo = titulo,
                        descripcion = descripcion,
                        progreso = progreso.toIntOrNull() ?: 0,
                        fechaEntrega = fechaLong,
                        prioridad = prioridad
                    )
                    onGuardar(nueva)
                }
            }
        },
        onBack = onBack
    )
}

/**
 * Lógica de negocio para construir el estado del formulario y sus validaciones.
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
    
    // Título: Obligatorio, 3-80 caracteres.
    if (titulo.isBlank()) {
        errores["titulo"] = "El título es obligatorio"
    } else if (titulo.length < 3) {
        errores["titulo"] = "Mínimo 3 caracteres"
    } else if (titulo.length > 80) {
        errores["titulo"] = "Máximo 80 caracteres"
    }
    
    // Descripción: Opcional, máximo 240 caracteres.
    if (descripcion.length > 240) {
        errores["descripcion"] = "Máximo 240 caracteres"
    }
    
    // Fecha: dd/mm/aaaa, no anterior a hoy.
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }
    try {
        if (fecha.isNotBlank()) {
            val date = format.parse(fecha)
            if (date != null) {
                val cal = Calendar.getInstance().apply { 
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                if (date.before(cal.time)) {
                    errores["fecha"] = "No puede ser anterior a hoy"
                }
            }
        } else {
            errores["fecha"] = "Fecha obligatoria"
        }
    } catch (e: Exception) {
        errores["fecha"] = "Formato inválido (dd/mm/aaaa)"
    }

    // Progreso: Numérico entre 0 y 100.
    val progInt = progreso.toIntOrNull()
    if (progInt == null) {
        errores["progreso"] = "Debe ser un número entero"
    } else if (progInt < 0 || progInt > 100) {
        errores["progreso"] = "Debe estar entre 0 y 100"
    }

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

// --- Secciones de contenido teórico conservadas ---

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

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    Labo_android_semana_02Theme {
        MainApp()
    }
}
