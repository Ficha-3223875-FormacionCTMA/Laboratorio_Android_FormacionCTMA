package com.example.labo_android_semana_02.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.ui.FormularioActividadUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioActividad(
    uiState: FormularioActividadUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onFechaChange: (String) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onProgresoChange: (String) -> Unit,
    onTomarFoto: () -> Unit,
    onSubirFoto: () -> Unit,
    onSubirArchivo: () -> Unit,
    onGuardar: () -> Unit,
    onBack: () -> Unit,
    onLimpiar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val triggerHaptic = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulario de Actividad con Evidencias") },
                navigationIcon = {
                    TextButton(onClick = {
                        triggerHaptic()
                        onBack()
                    }) {
                        Text("< Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                label = { Text("Título *") },
                isError = uiState.errores.containsKey("titulo"),
                supportingText = { uiState.errores["titulo"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción") },
                isError = uiState.errores.containsKey("descripcion"),
                supportingText = { uiState.errores["descripcion"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.fecha,
                onValueChange = onFechaChange,
                label = { Text("Fecha de entrega (dd/mm/aaaa) *") },
                placeholder = { Text("Ej: 25/12/2026") },
                isError = uiState.errores.containsKey("fecha"),
                supportingText = { uiState.errores["fecha"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Prioridad", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Prioridad.entries.forEach { p ->
                    FilterChip(
                        selected = uiState.prioridad == p,
                        onClick = {
                            triggerHaptic()
                            onPrioridadChange(p)
                        },
                        label = { Text(p.name) }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.progreso,
                onValueChange = onProgresoChange,
                label = { Text("Progreso (%)") },
                isError = uiState.errores.containsKey("progreso"),
                supportingText = { uiState.errores["progreso"]?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Sección de Evidencias (Fotos y Archivos)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Evidencia (Fotos y Archivos)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                triggerHaptic()
                                onTomarFoto()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tomar Foto", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = {
                                triggerHaptic()
                                onSubirFoto()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Subir Imagen", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            triggerHaptic()
                            onSubirArchivo()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Subir Archivo de Evidencia", style = MaterialTheme.typography.labelMedium)
                    }

                    val foto = uiState.evidenciaFotoUri
                    if (!foto.isNullOrBlank()) {
                        Text(
                            text = "Foto adjunta: ${foto.takeLast(30)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val archivo = uiState.evidenciaArchivoUri
                    if (!archivo.isNullOrBlank()) {
                        Text(
                            text = "Archivo adjunto: ${archivo.takeLast(30)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    triggerHaptic()
                    onGuardar()
                },
                enabled = uiState.puedeGuardar && !uiState.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Actividad")
                }
            }

            OutlinedButton(
                onClick = {
                    triggerHaptic()
                    onLimpiar()
                },
                enabled = !uiState.estaVacio && !uiState.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Limpiar formulario" }
            ) {
                Text("Limpiar")
            }
        }
    }
}
