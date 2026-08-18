# Tarjetas de Actividad Expandibles

Se ha implementado una mejora en la interfaz de usuario que permite visualizar la información detallada de cada actividad mediante un gesto de expansión.

## Descripción de la Funcionalidad

### 1. Interacción de Expansión
- **Activación**: Al tocar (hacer clic) en cualquier parte de la tarjeta de una actividad, esta cambia su tamaño para mostrar contenido adicional.
- **Animación**: Se utiliza `animateContentSize` para que la transición entre el estado contraído y expandido sea fluida y visualmente agradable.

### 2. Contenido Expandido
Cuando una tarjeta se expande, se muestran los siguientes detalles que anteriormente estaban ocultos o truncados:
- **Título Completo**: Se elimina el límite de una sola línea, permitiendo que títulos largos se vean en su totalidad.
- **Descripción Completa**: Se elimina el límite de líneas y el truncamiento, permitiendo leer todo el texto ingresado al crear la tarea.
- **Detalle de Fecha**: Se muestra la fecha de entrega completa en una línea dedicada (ej: "Fecha de entrega: 18 Ago 2026").

### 3. Estado Contraído (Vista Previa)
- Para mantener la lista limpia y legible, la vista contraída sigue mostrando solo una línea con la fecha corta y el inicio de la descripción, usando puntos suspensivos si el texto es muy largo.

## Detalles Técnicos
- **Estado Interno**: Cada `TarjetaActividad` gestiona su propio estado `expandida` mediante `remember` y `mutableStateOf`.
- **Formato de Fecha**: Se actualizó el formato a `dd MMM yyyy` para ofrecer mayor precisión en la vista detallada.
