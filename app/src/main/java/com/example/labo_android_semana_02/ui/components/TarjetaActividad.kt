package com.example.labo_android_semana_02.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onClick: (ActividadFormativa) -> Unit,
    onDelete: (ActividadFormativa) -> Unit,
    onEdit: (ActividadFormativa) -> Unit,
    onToggleStatus: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoy = Calendar.getInstance().timeInMillis
    val diferenciaMillis = actividad.fechaEntrega - hoy
    val diasRestantes = (diferenciaMillis / (24 * 60 * 60 * 1000L)).toInt()
    
    val tresDiasEnMillis = 3 * 24 * 60 * 60 * 1000L
    val esUrgente = actividad.progreso < 100 && diferenciaMillis < tresDiasEnMillis
    val esTerminada = actividad.progreso == 100

    val colorIndicador = when {
        esTerminada -> Color(0xFF00BFA5)
        esUrgente -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    val formatoFecha = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val fechaTexto = formatoFecha.format(Date(actividad.fechaEntrega))

    var expandida by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { 
                expandida = !expandida
                onClick(actividad) 
            }
            .semantics {
                contentDescription = "Actividad: ${actividad.titulo}, ${if(esTerminada) "Terminada" else "Faltan $diasRestantes días"}"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esTerminada) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (esTerminada) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorIndicador,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(12.dp, 40.dp)
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = esTerminada,
                        onCheckedChange = { onToggleStatus(actividad) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00BFA5))
                    )
                    Text(
                        text = actividad.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = if (expandida) Int.MAX_VALUE else 1,
                        overflow = if (expandida) TextOverflow.Clip else TextOverflow.Ellipsis,
                        color = if (esTerminada) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = if (expandida) actividad.descripcion else "$fechaTexto - ${actividad.descripcion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expandida) Int.MAX_VALUE else 1,
                    overflow = if (expandida) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 48.dp)
                )
                
                if (expandida) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fecha de entrega: $fechaTexto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { actividad.progreso / 100f },
                    modifier = Modifier.fillMaxWidth().padding(start = 48.dp),
                    color = colorIndicador,
                    trackColor = colorIndicador.copy(alpha = 0.2f),
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = com.example.labo_android_semana_02.domain.calcularTextoTiempoRestante(actividad.progreso, actividad.fechaEntrega),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorIndicador
                )
                Row {
                    TextButton(
                        onClick = { onEdit(actividad) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.sizeIn(minWidth = 32.dp)
                    ) {
                        Text("E", fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = { onDelete(actividad) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.sizeIn(minWidth = 32.dp)
                    ) {
                        Text("X", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaActividadPreview() {
    Labo_android_semana_02Theme {
        Column(modifier = Modifier.padding(16.dp)) {
            TarjetaActividad(
                actividad = ActividadFormativa(1, "Actividad Urgente", "Vence pronto.", 50, System.currentTimeMillis() + 86400000),
                onClick = {}, onDelete = {}, onEdit = {}, onToggleStatus = {}
            )
        }
    }
}
