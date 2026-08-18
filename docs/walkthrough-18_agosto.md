# Walkthrough: Edición y Tiempo Restante (18 de Agosto)

He implementado las mejoras solicitadas para permitir la edición de actividades y mejorar la visualización de los plazos de entrega.

## Cambios Realizados

### 1. Interfaz de Tarjeta (TarjetaActividad.kt)
- **Sustitución de Porcentaje**: Se eliminó el porcentaje de la esquina inferior.
- **Días Restantes**: Se añadió un indicador de texto que muestra "Faltan X d", "Hoy" o "Vencida" según la fecha de entrega.
- **Botón de Edición**: Se incluyó el botón **"E"** al lado del botón de eliminar ("X").

### 2. Lógica de Edición (MainActivity.kt)
- **Diálogo Dinámico**: El diálogo ahora permite tanto crear como editar actividades.
- **Persistencia de Cambios**: Al editar, se actualiza la actividad correspondiente en la lista y la UI se refresca automáticamente manteniendo el orden de prioridad.

## Verificación
- La build fue exitosa.
- Se comprobó que el tiempo restante se calcula correctamente desde la fecha actual.
- Se verificó que la edición modifica todos los campos (título, descripción y días).
