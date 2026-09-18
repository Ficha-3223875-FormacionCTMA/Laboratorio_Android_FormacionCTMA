package com.example.labo_android_semana_02.ui

import com.example.labo_android_semana_02.domain.Reporte

/**
 * HU-17: resumen numérico del listado (pendientes vs. completados).
 */
data class ResumenListado(
    val pendientes: Int = 0,
    val completados: Int = 0
) {
    val total: Int get() = pendientes + completados
}

/**
 * HU-17 (CA-17.2): calcula el resumen a partir de la lista vigente.
 * Al derivarse de la propia lista, los contadores se recalculan de forma
 * automática cada vez que se agrega, elimina o cambia el estado de un registro.
 */
fun List<Reporte>.calcularResumen(): ResumenListado = ResumenListado(
    pendientes = count { !it.resuelto },
    completados = count { it.resuelto }
)

/**
 * HU-22 (CA-22.2): texto formateado (ID y título) que se envía al portapapeles.
 */
fun formatoPortapapeles(reporte: Reporte): String = "#${reporte.id} - ${reporte.titulo}"
