package com.example.labo_android_semana_02.domain

object ActividadRepository {
    val actividades = listOf(
        ActividadFormativa(1, "Kotlin Básico", "Estudio de sintaxis, variables y tipos de datos en Kotlin.", 100, 10, Prioridad.BAJA),
        ActividadFormativa(2, "Fundamentos de Android", "Arquitectura de Android, ciclo de vida y componentes básicos.", 80, 5, Prioridad.MEDIA),
        ActividadFormativa(3, "Jetpack Compose UI", "Construcción de interfaces modernas y declarativas con Compose.", 60, 2, Prioridad.ALTA),
        ActividadFormativa(4, "Gestión de Estado", "Uso de remember, mutableStateOf y ViewModel para el estado.", 45, 3, Prioridad.ALTA),
        ActividadFormativa(5, "Navegación en Compose", "Implementación de navegación entre pantallas y paso de argumentos.", 20, 7, Prioridad.MEDIA),
        ActividadFormativa(6, "Acceso a Datos con Room", "Persistencia local de datos utilizando la librería Room.", 0, 12, Prioridad.BAJA),
        ActividadFormativa(7, "Retrofit y APIs REST", "Consumo de servicios web y manejo de JSON con Retrofit.", 0, 8, Prioridad.MEDIA),
        ActividadFormativa(8, "Corrutinas en Kotlin", "Manejo de programación asíncrona y flujos de trabajo en segundo plano.", 10, 4, Prioridad.ALTA),
        ActividadFormativa(9, "Inyección de Dependencias", "Uso de Hilt para gestionar las dependencias de la aplicación.", 5, 15, Prioridad.BAJA),
        ActividadFormativa(10, "Pruebas Unitarias", "Escritura de tests para verificar la lógica de negocio del dominio.", 0, 6, Prioridad.MEDIA)
    )
}
