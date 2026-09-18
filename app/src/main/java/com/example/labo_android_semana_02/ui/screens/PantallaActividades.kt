package com.example.labo_android_semana_02.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.Reporte
import com.example.labo_android_semana_02.ui.ListadoUiState
import com.example.labo_android_semana_02.ui.calcularResumen
import com.example.labo_android_semana_02.ui.components.TarjetaReporte
import com.example.labo_android_semana_02.ui.formatoPortapapeles

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
    onRetry: () -> Unit,
    temaOscuro: Boolean = false,
    onToggleTema: (Boolean) -> Unit = {},
    onToggleFavorito: (Reporte) -> Unit = {}
) {
    // HU-18: reporte pendiente de confirmación de borrado (null = diálogo cerrado)
    var reporteAEliminar by remember { mutableStateOf<Reporte?>(null) }

    // HU-22: utilidades del sistema para copiar y notificar
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val onCopiar: (Reporte) -> Unit = { reporte ->
        clipboardManager.setText(AnnotatedString(formatoPortapapeles(reporte)))
        Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    val reportes = (uiState as? ListadoUiState.Contenido)?.reportes ?: emptyList()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("ReportaCTMA", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        // HU-16 (CA-16.1): control para alternar entre modo claro y oscuro
                        Icon(
                            imageVector = if (temaOscuro) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Switch(
                            checked = temaOscuro,
                            onCheckedChange = onToggleTema,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .semantics { contentDescription = "Cambiar tema de la aplicación" }
                        )
                    }
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
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // HU-17 (CA-17.1): resumen numérico siempre visible en la parte superior
            if (uiState !is ListadoUiState.Cargando) {
                val resumen = reportes.calcularResumen()
                ResumenContadores(
                    pendientes = resumen.pendientes,
                    completados = resumen.completados
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
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
                                        onDelete = { reporteAEliminar = it },
                                        onEdit = onEditReporte,
                                        onToggleStatus = onToggleStatus,
                                        onToggleFavorito = onToggleFavorito,
                                        onCopiar = onCopiar
                                    )
                                }
                            }
                            if (completados.isNotEmpty()) {
                                item { SeccionHeader("RESUELTOS", Color(0xFF00BFA5)) }
                                items(completados, key = { it.id }) { reporte ->
                                    TarjetaReporte(
                                        reporte = reporte,
                                        onClick = onReporteClick,
                                        onDelete = { reporteAEliminar = it },
                                        onEdit = onEditReporte,
                                        onToggleStatus = onToggleStatus,
                                        onToggleFavorito = onToggleFavorito,
                                        onCopiar = onCopiar
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // HU-18 (CA-18.1 y CA-18.2): diálogo modal de confirmación de borrado
    reporteAEliminar?.let { reporte ->
        AlertDialog(
            onDismissRequest = { reporteAEliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar este elemento?") },
            confirmButton = {
                TextButton(onClick = {
                    // CA-18.3: solo aquí se remueve el registro de la fuente de verdad
                    onDeleteReporte(reporte)
                    reporteAEliminar = null
                }) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { reporteAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * HU-17: tarjeta compacta con el conteo de pendientes y completados.
 * Los valores se reciben ya calculados desde el listado, por lo que se recalculan
 * automáticamente en cada recomposición (agregar, eliminar o marcar/desmarcar).
 */
@Composable
fun ResumenContadores(
    pendientes: Int,
    completados: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContadorItem(
                etiqueta = "Pendientes",
                valor = pendientes,
                color = MaterialTheme.colorScheme.primary
            )
            VerticalDivider(modifier = Modifier.height(32.dp))
            ContadorItem(
                etiqueta = "Completados",
                valor = completados,
                color = Color(0xFF00BFA5)
            )
        }
    }
}

@Composable
private fun ContadorItem(etiqueta: String, valor: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics { contentDescription = "$etiqueta: $valor" }
    ) {
        Text(
            text = "$etiqueta: $valor",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
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
