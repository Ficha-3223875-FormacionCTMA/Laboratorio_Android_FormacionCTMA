package com.example.labo_android_semana_02.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.Reporte
import java.text.SimpleDateFormat
import java.util.*

/** HU-21 (CA-21.3): color de realce para el icono de favorito. */
private val ColorFavorito = Color(0xFFFFC107)

@Composable
fun TarjetaReporte(
    reporte: Reporte,
    onClick: (Reporte) -> Unit,
    onDelete: (Reporte) -> Unit,
    onEdit: (Reporte) -> Unit,
    onToggleStatus: (Reporte) -> Unit,
    onToggleFavorito: (Reporte) -> Unit = {},
    onCopiar: (Reporte) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val triggerHaptic = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }

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
            .clickable {
                triggerHaptic()
                expandida = !expandida
                onClick(reporte)
            }
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
                        onCheckedChange = {
                            triggerHaptic()
                            onToggleStatus(reporte)
                        },
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
                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onToggleFavorito(reporte)
                        },
                        modifier = Modifier.size(32.dp).semantics {
                            contentDescription = if (reporte.esFavorito) "Quitar de favoritos" else "Marcar como favorito"
                        }
                    ) {
                        Icon(
                            imageVector = if (reporte.esFavorito) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (reporte.esFavorito) ColorFavorito else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onCopiar(reporte)
                        },
                        modifier = Modifier.size(32.dp).semantics {
                            contentDescription = "Copiar información del reporte"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onEdit(reporte)
                        },
                        modifier = Modifier.size(32.dp).semantics {
                            contentDescription = "Editar reporte"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            triggerHaptic()
                            onDelete(reporte)
                        },
                        modifier = Modifier.size(32.dp).semantics {
                            contentDescription = "Eliminar reporte"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
