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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.ActividadRepository
import com.example.labo_android_semana_02.ui.screens.PantallaActividades
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme
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
    var selectedTab by remember { mutableStateOf(AgileTab.ACTIVIDADES) }
    val actividades = remember { mutableStateListOf<ActividadFormativa>().apply { addAll(ActividadRepository.actividades) } }
    var showDialog by remember { mutableStateOf(false) }

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
                    onActividadClick = { },
                    onDeleteActividad = { actividades.remove(it) },
                    onToggleStatus = { act ->
                        val index = actividades.indexOf(act)
                        if (index != -1) {
                            val nuevoProgreso = if (act.progreso == 100) 0 else 100
                            actividades[index] = act.copy(progreso = nuevoProgreso)
                        }
                    },
                    onAddClick = { showDialog = true },
                    onReiniciarFiltros = {
                        actividades.clear()
                        actividades.addAll(ActividadRepository.actividades)
                    }
                )
                AgileTab.MANIFESTO -> SeccionManifiesto()
                AgileTab.SCRUM -> SeccionScrum()
                AgileTab.TESTING -> SeccionPruebas()
            }
        }
    }

    if (showDialog) {
        DialogNuevaActividad(
            onDismiss = { showDialog = false },
            onConfirm = { titulo, desc, dias ->
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, dias)
                val nueva = ActividadFormativa(
                    id = (actividades.maxOfOrNull { it.id } ?: 0) + 1,
                    titulo = titulo,
                    descripcion = desc,
                    progreso = 0,
                    fechaEntrega = cal.timeInMillis
                )
                actividades.add(nueva)
                showDialog = false
            }
        )
    }
}

@Composable
fun DialogNuevaActividad(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var diasParaEntrega by remember { mutableStateOf("7") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Actividad", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = diasParaEntrega,
                    onValueChange = { if (it.all { char -> char.isDigit() }) diasParaEntrega = it },
                    label = { Text("Días para entrega") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val dias = diasParaEntrega.toIntOrNull() ?: 7
                    if (titulo.isNotBlank()) onConfirm(titulo, descripcion, dias) 
                },
                enabled = titulo.isNotBlank()
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

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
