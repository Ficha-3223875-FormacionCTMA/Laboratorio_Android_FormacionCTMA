package com.example.labo_android_semana_02.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.ui.ListadoUiState
import com.example.labo_android_semana_02.ui.components.TarjetaReporte // Suponiendo que la renombramos o adaptamos
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    uiState: ListadoUiState,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onReporteClick: (Reporte) -> Unit,
    onDeleteReporte: (Reporte) -> Unit,
    onEditReporte: (Reporte) -> Unit,
    onToggleStatus: (Reporte) -> Unit,
    onAddClick: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("ReportaCTMA", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = onBusquedaChange,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    placeholder = { Text("Buscar reporte...") },
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is ListadoUiState.Cargando -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando reportes...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is ListadoUiState.Vacio -> {
                    EstadoInformativo(
                        mensaje = "No se encontraron reportes.",
                        accionTexto = "Crear mi primer reporte",
                        onAccion = onAddClick
                    )
                }
                is ListadoUiState.Error -> {
                    EstadoInformativo(
                        mensaje = uiState.mensaje,
                        accionTexto = "Reintentar",
                        onAccion = onRetry
                    )
                }
                is ListadoUiState.Contenido -> {
                    val reportes = uiState.reportes
                    val pendientes = reportes.filter { !it.resuelto }
                    val completados = reportes.filter { it.resuelto }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (pendientes.isNotEmpty()) {
                            item { SeccionHeader("PENDIENTES", MaterialTheme.colorScheme.primary) }
                            items(pendientes, key = { it.id }) { reporte ->
                                TarjetaReporte(
                                    reporte = reporte,
                                    onClick = onReporteClick,
                                    onDelete = onDeleteReporte,
                                    onEdit = onEditReporte,
                                    onToggleStatus = onToggleStatus
                                )
                            }
                        }
                        if (completados.isNotEmpty()) {
                            item { SeccionHeader("RESUELTOS", Color(0xFF00BFA5)) }
                            items(completados, key = { it.id }) { reporte ->
                                TarjetaReporte(
                                    reporte = reporte,
                                    onClick = onReporteClick,
                                    onDelete = onDeleteReporte,
                                    onEdit = onEditReporte,
                                    onToggleStatus = onToggleStatus
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionHeader(titulo: String, color: Color) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun EstadoInformativo(
    mensaje: String,
    accionTexto: String,
    onAccion: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(text = mensaje, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAccion) { Text(accionTexto) }
        }
    }
}
