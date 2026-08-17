package com.example.labo_android_semana_02.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorPrioridad = when (actividad.prioridad) {
        Prioridad.ALTA -> MaterialTheme.colorScheme.error
        Prioridad.MEDIA -> MaterialTheme.colorScheme.tertiary
        Prioridad.BAJA -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(actividad) }
            .semantics {
                contentDescription = "Actividad: ${actividad.titulo}, progreso ${actividad.progreso} por ciento, prioridad ${actividad.prioridad}"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridad
            Surface(
                color = colorPrioridad,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(12.dp, 40.dp)
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = actividad.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { actividad.progreso / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${actividad.progreso}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaActividadPreview() {
    Labo_android_semana_02Theme {
        Column(modifier = Modifier.padding(16.dp)) {
            TarjetaActividad(
                actividad = ActividadFormativa(
                    1, 
                    "Título muy largo que debería truncarse con puntos suspensivos en la interfaz", 
                    "Esta es una descripción detallada que también debería limitarse a máximo dos líneas para mantener la consistencia visual de la tarjeta.", 
                    100, 
                    5, 
                    Prioridad.ALTA
                ),
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaActividad(
                actividad = ActividadFormativa(
                    2, 
                    "Progreso mínimo", 
                    "Actividad recién iniciada.", 
                    0, 
                    10, 
                    Prioridad.BAJA
                ),
                onClick = {}
            )
        }
    }
}
