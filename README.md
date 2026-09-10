# Laboratorio Android — Mi Formación CTMA (ReportaCTMA)

Repositorio de laboratorio para proyectos Android escritos en Kotlin, desarrollado como caso integrador acumulativo de la ruta de formación **Desarrollo de aplicaciones móviles Android** (programa ADSO, SENA).

## Descripción
Proyecto Android evolucionado semana a semana. En esta etapa final, la aplicación se ha transformado en **ReportaCTMA**, un sistema reactivo y persistente para la gestión de reportes y actividades formativas.

## Tecnologías y Arquitectura
* **Kotlin 2.0.21** con motor **KSP**.
* **Jetpack Compose** para una UI declarativa y accesible.
* **Architecture Components**: ViewModel, StateFlow y Navigation Compose.
* **Room Database**: Fuente Única de Verdad (SSoT) para reportes y categorías.
* **DataStore Preferences**: Persistencia de configuraciones de usuario (filtros/orden).
* **Coroutines & Flow**: Manejo asíncrono y reactivo de datos con cancelación automática de tareas.

### Decisiones de Arquitectura
1. **Flujo de Datos (UDF)**: UI -> Eventos -> ViewModel -> Repositorio -> Room -> Flow -> UI.
2. **Dispatchers**: Se utiliza `Dispatchers.IO` implícitamente a través de Room y DataStore. El ViewModel opera en el `viewModelScope` principal para orquestar los estados.
3. **Gestión de Estado**: Se implementó `ListadoUiState` (Cargando, Contenido, Vacio, Error) y `OperacionUiState` (Ideal, Cargando, Exito, Error) para una UI desacoplada y resiliente.

## Instalación y Ejecución
1. Clona el repositorio: `git clone https://github.com/JuanManuelCM3331/Laboratorio_Android_repo.git`
2. Abre el proyecto en Android Studio.
3. Ejecuta la app en un dispositivo o emulador.

## Tests
* **Unitarios e Instrumentados**: `./gradlew connectedAndroidTest`. 
* Se incluyen pruebas para validar las 14 Historias de Usuario (HU-01 a HU-14), incluyendo migraciones de base de datos y reactividad de flujos.
* **Garantía de Calidad**: Se utiliza `runTest` y repositorios simulados para evitar bloqueos y asegurar transiciones de estado correctas.

---

## Fundamentos Teóricos y Avances

### Semana 6 & Final · Persistencia y Estados Avanzados
**Meta:** Implementar persistencia robusta, estados reactivos complejos y validación de flujos asíncronos.

**Conceptos Clave:**
* **Single Source of Truth (SSoT)**: Room es la única fuente de verdad. El estado de la UI nace de la observación de la base de datos.
* **Reactive Combination**: Uso de `combine` para mezclar reportes de Room, preferencias de DataStore y búsquedas del usuario.
* **Cancelación Eficiente**: Implementación de `mapLatest` y `flatMapLatest` para ignorar peticiones de búsqueda obsoletas y optimizar recursos.
* **Lifecycle Awareness**: Recolección de estados con `collectAsStateWithLifecycle` para detener la recolección cuando la app está en segundo plano.

**Casos de Prueba Validados:**
| Caso | Acción | Resultado |
|---|---|---|
| CA-14.1 | Abrir sin actividades | Transición limpia Cargando -> Vacío. |
| CA-14.2 | Insertar actividad | Actualización reactiva instantánea vía Flow. |
| CA-14.3 | Cambiar filtro | Persistencia en DataStore y recálculo por `combine`. |
| CA-14.4 | Búsquedas rápidas | Cancelación de Jobs previos; se procesa el último término. |
| CA-14.6 | Fallo en Repo | Interfaz de Error con botón de Reintentar. |

---

## Uso de Inteligencia Artificial
Este proyecto ha sido desarrollado con el apoyo de herramientas de IA, validando cada decisión técnica y corrección de código para asegurar la adherencia a las mejores prácticas de Google para Android.

## Contribuciones
1. Fork del repositorio.
2. Branch `feature/lab-final`.
3. Pull Request con descripción de cambios.
