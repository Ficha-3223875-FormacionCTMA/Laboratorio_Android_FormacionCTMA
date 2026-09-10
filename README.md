# ReportaCTMA — Mi Formación Profesional

Este es el proyecto integrador del programa de formación **Desarrollo de aplicaciones móviles Android** (ADSO, SENA). La aplicación ha evolucionado semana a semana desde un entorno básico hasta convertirse en un sistema robusto de gestión de actividades y reportes con arquitectura profesional, persistencia local y sincronización remota.

## 🚀 Funcionalidades Principales

### Gestión Integral de Actividades (CRUD)
*   **Registro**: Creación de actividades con título, descripción y fecha de entrega.
*   **Edición**: Modificación completa de cualquier campo de tareas existentes.
*   **Eliminación**: Limpieza de la lista mediante borrado permanente.
*   **Control de Estado**: Marcado rápido como "Resuelto" mediante Checkbox, con actualización automática de progreso al 100%.

### Inteligencia de Negocio y UI
*   **Prioridad Automática**: El sistema calcula la urgencia basándose en la fecha de entrega. Las tareas con menos de 3 días de plazo se resaltan en **rojo**.
*   **Ordenamiento Dinámico**: Las tareas pendientes se ordenan cronológicamente (vencimiento próximo primero).
*   **Separación de Estados**: Vista dividida entre tareas "Pendientes" y "Completadas".
*   **Tarjetas Expandibles**: Interfaz limpia que muestra detalles (descripción completa y fecha exacta) solo al interactuar con la tarjeta mediante animaciones fluidas.
*   **Búsqueda en Tiempo Real**: Filtrado de reportes por título o descripción con cancelación automática de búsquedas obsoletas.

### Persistencia y Sincronización
*   **Offline-First**: Los datos se consultan siempre desde la base de datos local (**Room**), garantizando disponibilidad sin internet.
*   **Sincronización Remota**: Integración con API REST (**Retrofit**) para respaldo y actualización de datos en la nube.
*   **Preferencias**: Guardado automático del orden y filtros elegidos por el usuario mediante **DataStore**.

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue los principios de **Clean Architecture** y el patrón **MVVM (Model-View-ViewModel)**, asegurando un código escalable y testeable.

### Capas
1.  **UI (Capa de Presentación)**:
    *   Componentes **Stateless** en Jetpack Compose.
    *   Gestión de estados complejos mediante interfaces selladas (`ListadoUiState`, `OperacionUiState`).
    *   Recolección de flujos consciente del ciclo de vida (`collectAsStateWithLifecycle`).
2.  **Domain (Capa de Negocio)**:
    *   Modelos de datos puros (`ActividadFormativa`, `Reporte`).
    *   Reglas de validación y lógica de cálculo de tiempos (`ReglasActividad.kt`).
    *   Definición de contratos mediante interfaces de repositorio.
3.  **Data (Capa de Datos)**:
    *   **Room**: Implementación de DAOs y transacciones atómicas.
    *   **Retrofit**: Consumo de servicios remotos y manejo de DTOs.
    *   **Mappers**: Transformación limpia entre entidades de BD, modelos de API y objetos de dominio.
    *   **Repositorios**: Coordinación entre la fuente de datos local y remota (Single Source of Truth).

---

## 🛠️ Stack Tecnológico

*   **Lenguaje**: Kotlin 2.0.21 (Motor K2).
*   **UI**: Jetpack Compose con Material 3.
*   **Navegación**: Navigation Compose con paso de argumentos seguros (IDs).
*   **Base de Datos**: Room 2.6.1 con exportación de esquemas JSON.
*   **Red**: Retrofit 2.11.0 + OkHttp (Timeouts y Logging configurados).
*   **Serialización**: Kotlinx Serialization.
*   **Asincronía**: Coroutines & Flow (StateFlow, SharedFlow).
*   **Procesamiento**: KSP (Kotlin Symbol Processing).

---

## 🧪 Estrategia de Calidad y Pruebas

El proyecto cuenta con una suite unificada de **más de 20 pruebas instrumentadas** localizadas en `HistoriasUsuarioTest.kt`.

*   **HU-01 a HU-12**: Validación de lógica de UI, State Hoisting y reglas de negocio.
*   **HU-13**: Pruebas de persistencia Room y **Migración de Esquema (v1 a v2)** sin pérdida de datos.
*   **HU-14**: Pruebas de concurrencia, cancelación de corrutinas y estados reactivos.
*   **HU-15**: Validación de sincronización remota y resiliencia ante fallos de red.

**Comando para ejecutar pruebas**:
```bash
./gradlew connectedAndroidTest
```

---

## git Estructura de Documentación (`/docs`)

Para una trazabilidad completa, el proyecto incluye los siguientes documentos:
*   [HU.md](file:///C:/Users/JuanMa3331/labo_android_semana_02/docs/HU.md): Historias de Usuario y Criterios de Aceptación (HU-01 a HU-15).
*   [PLAN_PRUEBAS.md](file:///C:/Users/JuanMa3331/labo_android_semana_02/docs/PLAN_PRUEBAS.md): Casos de prueba detallados (TC-01 a TC-64).
*   [RIESGOS.md](file:///C:/Users/JuanMa3331/labo_android_semana_02/docs/RIESGOS.md): Matriz de riesgos técnicos y de negocio con planes de mitigación.
*   [contrato_api.md](file:///C:/Users/JuanMa3331/labo_android_semana_02/docs/contrato_api.md): Especificación técnica del contrato JSON y códigos HTTP.

---

## 🤖 Uso de Inteligencia Artificial
Este desarrollo ha sido asistido por herramientas de IA, las cuales han sido utilizadas para:
*   Generación de código repetitivo (*boilerplate*) y mappers.
*   Optimización de consultas SQL y lógica de corrutinas.
*   Refactorización de arquitectura para cumplir con los estándares de Google.
*   Validación y creación de casos de prueba exhaustivos.

Cada línea de código generada ha sido revisada, ajustada y validada manualmente para garantizar la integridad y el aprendizaje del proceso.
