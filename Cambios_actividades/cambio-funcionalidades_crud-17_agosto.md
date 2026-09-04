# Documentación de Funcionalidades CRUD y Estados

Esta sección detalla la implementación de las capacidades interactivas añadidas al sistema de gestión de actividades formativas.

## Funcionalidades Implementadas

### 1. Creación de Actividades (Create)
Se ha integrado un flujo completo para la adición de nuevas tareas:
- **Interfaz**: Botón Flotante (FAB) en la esquina inferior derecha de la pestaña "Actividades". Representado por el símbolo `+` para minimizar el uso de recursos.
- **Diálogo**: Un `AlertDialog` que captura:
    - **Título**: Campo obligatorio para identificar la tarea.
    - **Descripción**: Detalle opcional de la actividad.
    - **Prioridad**: Selección dinámica mediante `FilterChip` (ALTA, MEDIA, BAJA).
- **Lógica**: Generación automática de ID único basado en el máximo existente y persistencia en el estado mutable de la sesión actual.

### 2. Eliminación de Actividades (Delete)
Permite la limpieza y gestión de la lista:
- **Interfaz**: Botón de texto "X" resaltado en color de error (rojo) dentro de cada `TarjetaActividad`.
- **Lógica**: Eliminación inmediata del elemento del `SnapshotStateList` lo que dispara la recomposición de la UI.

### 3. Actualización de Estado (Update)
Gestión del ciclo de vida de la actividad:
- **Interfaz**: `Checkbox` integrado junto al título de la actividad.
- **Comportamiento**: 
    - Al marcarse, el progreso se establece en **100%** (Actividad completada).
    - Al desmarcarse, el progreso vuelve a **0%** (Actividad pendiente).
    - Los indicadores visuales (barra de progreso y porcentajes) se actualizan en tiempo real.

## Decisiones Técnicas y Optimización

### Rendimiento de Compilación (Build Speed)
Para mantener el proyecto ligero y evitar los tiempos de espera prolongados detectados anteriormente:
- **Eliminación de Iconos Vectoriales**: Se evitó el uso de `androidx.compose.material:material-icons-extended`.
- **Representación Textual/Geométrica**: Las acciones se representan mediante caracteres (`+`, `X`) y formas básicas (`Box`, `CircleShape`), logrando una UI intuitiva sin la sobrecarga de librerías externas pesadas.

### Manejo de Estado
- Se migró de una lista estática en el Repositorio a un `SnapshotStateList` (`mutableStateListOf`) en el nivel superior de la aplicación (`MainApp`), permitiendo que Jetpack Compose reaccione automáticamente a cualquier cambio en los datos.
