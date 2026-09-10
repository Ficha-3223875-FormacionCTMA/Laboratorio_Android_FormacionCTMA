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
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.Reporte
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TarjetaReporte(
    reporte: Reporte,
    onClick: (Reporte) -> Unit,
    onDelete: (Reporte) -> Unit,
    onEdit: (Reporte) -> Unit,
    onToggleStatus: (Reporte) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoy = System.currentTimeMillis()
    val diferenciaMillis = reporte.fecha - hoy
    val diasRestantes = (diferenciaMillis / (24 * 60 * 60 * 1000L)).toInt()
    val esUrgente = !reporte.resuelto && diferenciaMillis < (3 * 24 * 60 * 60 * 1000L)

    val colorIndicador = when {
        reporte.resuelto -> Color(0xFF00BFA5)
        esUrgente -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val fechaTexto = format.format(Date(reporte.fecha))

    var expandida by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expandida = !expandida; onClick(reporte) }
            .semantics {
                contentDescription = "Reporte: ${reporte.titulo}, ${if(reporte.resuelto) "Resuelto" else "Faltan $diasRestantes días"}"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reporte.resuelto) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                        checked = reporte.resuelto,
                        onCheckedChange = { onToggleStatus(reporte) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00BFA5))
                    )
                    Text(
                        text = reporte.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = if (expandida) Int.MAX_VALUE else 1,
                        overflow = if (expandida) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = if (expandida) reporte.descripcion else "$fechaTexto - ${reporte.descripcion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expandida) Int.MAX_VALUE else 1,
                    overflow = if (expandida) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 48.dp)
                )
                
                if (expandida) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fecha: $fechaTexto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = when {
                        reporte.resuelto -> "OK"
                        diasRestantes < 0 -> "Vencido"
                        diasRestantes == 0 -> "Hoy"
                        else -> "Faltan $diasRestantes d"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorIndicador
                )
                Row {
                    TextButton(onClick = { onEdit(reporte) }, modifier = Modifier.sizeIn(minWidth = 32.dp)) {
                        Text("E", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { onDelete(reporte) }, modifier = Modifier.sizeIn(minWidth = 32.dp)) {
                        Text("X", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
