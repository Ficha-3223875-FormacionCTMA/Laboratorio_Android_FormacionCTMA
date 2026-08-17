package com.example.labo_android_semana_02.domain

fun validarActividad(
    titulo: String,
    progreso: Int
): List<String> {
    val errores = mutableListOf<String>()

    if (titulo.isBlank()) {
        errores.add("El título es obligatorio")
    }

    if (progreso !in 0..100) {
        errores.add("El progreso debe estar entre 0 y 100")
    }

    return errores
}

fun estadoActividad(
    progreso: Int,
    diasRestantes: Int
): String {
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
    return actividades.filter {
        it.progreso < 100 && it.diasRestantes <= 3
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