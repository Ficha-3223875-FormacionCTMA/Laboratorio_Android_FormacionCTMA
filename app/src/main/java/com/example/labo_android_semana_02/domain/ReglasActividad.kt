package com.example.labo_android_semana_02.domain

import java.util.Calendar

fun validarActividad(
    titulo: String,
    progreso: Int
): List<String> {
    val errores = mutableListOf<String>()
    if (titulo.isBlank()) errores.add("El título es obligatorio")
    if (progreso !in 0..100) errores.add("El progreso debe estar entre 0 y 100")
    return errores
}

fun estadoActividad(
    progreso: Int,
    fechaEntrega: Long
): String {
    val hoy = Calendar.getInstance().timeInMillis
    val diasRestantes = ((fechaEntrega - hoy) / (24 * 60 * 60 * 1000)).toInt()
    
    return when {
        progreso == 100 -> "COMPLETADA"
        diasRestantes < 0 -> "VENCIDA"
        progreso > 0 -> "EN PROCESO"
        else -> "PENDIENTE"
    }
}

fun actividadesUrgentes(
    actividades: List<ActividadFormativa>
): List<ActividadFormativa> {
    val hoy = Calendar.getInstance().timeInMillis
    val tresDiasEnMillis = 3 * 24 * 60 * 60 * 1000L
    return actividades.filter {
        it.progreso < 100 && (it.fechaEntrega - hoy) <= tresDiasEnMillis
    }
}

fun promedioProgreso(
    actividades: List<ActividadFormativa>
): Double {
    if (actividades.isEmpty()) return 0.0
    return actividades.map { it.progreso }.average()
}

fun buscarPorTitulo(
    actividades: List<ActividadFormativa>,
    texto: String
): List<ActividadFormativa> {
    val termino = texto.trim()
    return actividades.filter {
        it.titulo.contains(termino, ignoreCase = true)
    }
}
