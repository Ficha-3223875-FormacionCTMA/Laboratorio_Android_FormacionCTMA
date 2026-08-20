package com.example.labo_android_semana_02.domain

import java.util.Calendar

object ActividadRepository {
    private fun getFecha(diasDesdeHoy: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, diasDesdeHoy)
        return cal.timeInMillis
    }

    val actividades = listOf(
        ActividadFormativa(1, "Kotlin Básico", "Estudio de sintaxis.", 100, getFecha(10), Prioridad.BAJA),
        ActividadFormativa(2, "Fundamentos de Android", "Arquitectura básica.", 80, getFecha(5), Prioridad.MEDIA),
        ActividadFormativa(3, "Jetpack Compose UI", "Interfaces modernas.", 60, getFecha(2), Prioridad.ALTA),
        ActividadFormativa(4, "Gestión de Estado", "remember y ViewModel.", 45, getFecha(1), Prioridad.ALTA),
        ActividadFormativa(5, "Navegación en Compose", "Paso de argumentos.", 20, getFecha(7), Prioridad.MEDIA),
        ActividadFormativa(6, "Acceso a Datos con Room", "Persistencia local.", 0, getFecha(12), Prioridad.BAJA),
        ActividadFormativa(7, "Retrofit y APIs REST", "Consumo de servicios.", 0, getFecha(8), Prioridad.MEDIA),
        ActividadFormativa(8, "Corrutinas en Kotlin", "Programación asíncrona.", 10, getFecha(-1), Prioridad.ALTA),
        ActividadFormativa(9, "Inyección de Dependencias", "Uso de Hilt.", 5, getFecha(15), Prioridad.BAJA),
        ActividadFormativa(10, "Pruebas Unitarias", "Tests de lógica.", 0, getFecha(6), Prioridad.MEDIA)
    )
}
