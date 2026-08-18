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
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.ActividadRepository
import com.example.labo_android_semana_02.ui.components.TarjetaActividad
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit,
    onDeleteActividad: (ActividadFormativa) -> Unit,
    onEditActividad: (ActividadFormativa) -> Unit,
    onToggleStatus: (ActividadFormativa) -> Unit,
    onAddClick: () -> Unit,
    onReiniciarFiltros: () -> Unit = {}
) {
    val pendientes = actividades.filter { it.progreso < 100 }.sortedBy { it.fechaEntrega }
    val completadas = actividades.filter { it.progreso == 100 }.sortedByDescending { it.fechaEntrega }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formación CTMA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
        if (actividades.isEmpty()) {
            EstadoVacio(
                mensaje = "No hay actividades disponibles.",
                onAccion = onReiniciarFiltros,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pendientes.isNotEmpty()) {
                    item {
                        Text(
                            text = "PENDIENTES (${pendientes.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(items = pendientes, key = { it.id }) { actividad ->
                        TarjetaActividad(
                            actividad = actividad,
                            onClick = onActividadClick,
                            onDelete = onDeleteActividad,
                            onEdit = onEditActividad,
                            onToggleStatus = onToggleStatus
                        )
                    }
                }

                if (completadas.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "COMPLETADAS (${completadas.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BFA5), // AgileSecondary
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(items = completadas, key = { it.id }) { actividad ->
                        TarjetaActividad(
                            actividad = actividad,
                            onClick = onActividadClick,
                            onDelete = onDeleteActividad,
                            onEdit = onEditActividad,
                            onToggleStatus = onToggleStatus
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoVacio(
    mensaje: String,
    onAccion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "¡Ops!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensaje, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAccion) { Text("Recargar lista") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaActividadesPreview() {
    Labo_android_semana_02Theme {
        PantallaActividades(
            actividades = ActividadRepository.actividades,
            onActividadClick = {},
            onDeleteActividad = {},
            onEditActividad = {},
            onToggleStatus = {},
            onAddClick = {}
        )
    }
}
