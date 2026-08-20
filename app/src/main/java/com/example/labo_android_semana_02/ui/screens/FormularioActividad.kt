package com.example.labo_android_semana_02.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    onGuardar: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulario de Actividad") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
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
                        onClick = { onPrioridadChange(p) },
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onGuardar,
                enabled = uiState.puedeGuardar && !uiState.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Actividad")
                }
            }
        }
    }
}
