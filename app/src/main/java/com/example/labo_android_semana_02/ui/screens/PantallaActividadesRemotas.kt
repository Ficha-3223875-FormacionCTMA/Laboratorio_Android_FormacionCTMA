package com.example.labo_android_semana_02.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.ui.ActividadesUiState
import com.example.labo_android_semana_02.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividadesRemotas(
    uiState: ActividadesUiState,
    onRefresh: () -> Unit,
    onActividadClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividades Sincronizadas", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState) {
                is ActividadesUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ActividadesUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.mensaje, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefresh) { Text("Reintentar") }
                    }
                }
                is ActividadesUiState.Exito -> {
                    Column {
                        // Estado de actualización (Caché + Info Remota)
                        uiState.mensajeActualizacion?.let { msg ->
                            Surface(
                                color = if (uiState.esError) MaterialTheme.colorScheme.errorContainer 
                                        else MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                    if (uiState.esError) {
                                        TextButton(onClick = onRefresh) {
                                            Text("Reintentar", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.actividades, key = { it.id }) { actividad ->
                                TarjetaActividad(
                                    actividad = actividad,
                                    onClick = onActividadClick,
                                    onDelete = { /* No aplica en remoto en este lab */ },
                                    onEdit = { /* No aplica en remoto en este lab */ },
                                    onToggleStatus = { /* No aplica en remoto en este lab */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
