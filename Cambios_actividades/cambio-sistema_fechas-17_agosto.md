# Sistema de Prioridades Basado en Fechas

Se ha rediseñado el sistema para automatizar la urgencia de las tareas y mejorar la organización visual basada en el estado y los plazos de entrega.

## Cambios Implementados

### 1. Automatización de Prioridad
- **Eliminación de Prioridad Manual**: Ya no se solicita al usuario que elija entre "Alta, Media o Baja".
- **Lógica de Urgencia**: El sistema calcula los días restantes comparando la fecha actual con la fecha de entrega.
    - **Urgente (Rojo)**: Tareas con menos de 3 días para vencer y progreso menor al 100%.
    - **Pendiente (Gris/Azul)**: Tareas con más de 3 días de plazo.
    - **Completada (Verde)**: Tareas con 100% de progreso.

### 2. Organización de la Interfaz (Separación por Estados)
La pantalla de actividades ahora divide la lista en dos secciones claras:
- **Sección PENDIENTES**: Muestra las tareas no terminadas, ordenadas cronológicamente (las que vencen antes aparecen primero).
- **Sección COMPLETADAS**: Muestra las tareas con progreso del 100% en la parte inferior, separadas visualmente para reducir el ruido visual.

### 3. Flujo de Creación Simplificado
- El diálogo de "Nueva Actividad" ahora solicita **"Días para entrega"** (número) en lugar de la prioridad. Esto calcula automáticamente la fecha límite.

### 4. Detalles Visuales
- Se muestra la fecha de entrega formateada (ej: "18 Ago") en cada tarjeta.
- Las tareas completadas tienen una opacidad reducida y el indicador lateral cambia a verde esmeralda.

## Decisiones Técnicas
- **Domain Refactor**: Se eliminó el `enum Prioridad` y se añadió `fechaEntrega: Long` al modelo de datos.
- **Sorting Reactivo**: El ordenamiento se realiza dinámicamente en la UI cada vez que cambia el estado de una actividad (al marcar el checkbox, la tarea se mueve automáticamente de sección).
