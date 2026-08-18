# Decisiones de Diseño - Refactorización UI

Esta refactorización se centró en mejorar la arquitectura y la mantenibilidad de la aplicación siguiendo los principios de Jetpack Compose.

## Estructura de Paquetes
- `ui/components`: Contiene componentes reutilizables y sin estado como `TarjetaActividad`.
- `ui/screens`: Contiene las pantallas completas que orquestan los componentes.
- `domain`: Se mantuvo la lógica de negocio pero se centralizó el acceso a datos en `ActividadRepository`.

## UI y UX
- **Stateless Components**: `TarjetaActividad` no maneja su propio estado, lo que facilita las pruebas y la previsualización.
- **Lazy List Optimization**: Se implementó `key` en `LazyColumn` usando el ID único de la actividad para optimizar el recomposición.
- **Empty State**: Se añadió un manejo explícito para listas vacías con una acción clara para el usuario.
- **Accessibility**:
    - Se añadieron `semantics` y `contentDescription` detallados.
    - Se respetaron zonas táctiles adecuadas en las tarjetas.
    - Se utilizaron estilos de texto escalables (`sp`) y se limitó el truncamiento de texto para evitar pérdida de información crítica.

## Tema y Estilo
- Se integró la paleta de colores "Agile" solicitada por el usuario en el sistema de temas global de Material 3.
- Se eliminaron redundancias cromáticas y se centralizaron las tipografías.
