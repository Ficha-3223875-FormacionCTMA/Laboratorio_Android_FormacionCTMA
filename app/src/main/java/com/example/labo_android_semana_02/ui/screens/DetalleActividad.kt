package com.example.labo_android_semana_02.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.ActividadFormativa
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleActividad(
    actividad: ActividadFormativa?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("< Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (actividad == null) {
            Box(
                modifier = modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Actividad no encontrada", style = MaterialTheme.typography.headlineMedium)
            }
        } else {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaStr = format.format(Date(actividad.fechaEntrega))

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = actividad.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                
                Text(text = "Estado", fontWeight = FontWeight.Bold)
                Text(text = if (actividad.progreso == 100) "Completada" else "En proceso (${actividad.progreso}%)")
                
                Text(text = "Fecha de Entrega", fontWeight = FontWeight.Bold)
                Text(text = fechaStr)
                
                Text(text = "Prioridad", fontWeight = FontWeight.Bold)
                Text(text = actividad.prioridad.name)
                
                Text(text = "Descripción", fontWeight = FontWeight.Bold)
                Text(text = actividad.descripcion.ifBlank { "Sin descripción" })
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
