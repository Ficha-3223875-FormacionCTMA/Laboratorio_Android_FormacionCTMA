# Refactorización de UI y Pantalla de Actividades

Este plan detalla la refactorización de la interfaz de usuario para seguir las mejores prácticas de arquitectura en Android, separando componentes, pantallas y temas, e implementando una lista de actividades robusta y accesible.

## Cambios Propuestos

### Componentes de Dominio
#### [NEW] [ActividadRepository.kt](file:///C:/Users/JuanMa3331/labo_android_semana_02/app/src/main/java/com/example/labo_android_semana_02/domain/ActividadRepository.kt)
- Contendrá una lista estática de 10 actividades para pruebas y uso en la aplicación.

### Interfaz de Usuario (UI)

#### [NEW] [TarjetaActividad.kt](file:///C:/Users/JuanMa3331/labo_android_semana_02/app/src/main/java/com/example/labo_android_semana_02/ui/components/TarjetaActividad.kt)
- Composable sin estado que muestra la información de una `ActividadFormativa`.
- Incluirá callbacks para interacciones.
- Optimizado para accesibilidad (zonas táctiles, contraste, descripciones).

#### [NEW] [PantallaActividades.kt](file:///C:/Users/JuanMa3331/labo_android_semana_02/app/src/main/java/com/example/labo_android_semana_02/ui/screens/PantallaActividades.kt)
- Pantalla principal que utiliza `Scaffold` y `LazyColumn`.
- Manejo de estado vacío.
- Uso de claves estables (`key`) en la lista.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/JuanMa3331/labo_android_semana_02/app/src/main/java/com/example/labo_android_semana_02/MainActivity.kt)
- Limpieza de lógica de UI interna.
- Orquestación de la navegación básica (o simplificación según el nuevo esquema).

#### [MODIFY] [Theme.kt](file:///C:/Users/JuanMa3331/labo_android_semana_02/app/src/main/java/com/example/labo_android_semana_02/ui/theme/Theme.kt) y otros en `ui/theme/`
- Centralización de colores y tipografías para evitar repeticiones.

## Verificación Plan
- Previsualización en Compose (Previews) con datos de borde (títulos largos, progreso 0/100).
- Verificación de accesibilidad mediante inspección visual y parámetros de Composable.
- Build completo del proyecto.

## Documentación
#### [NEW] [decisiones_diseño.artifact.md](file:///C:/Users/JuanMa3331/labo_android_semana_02.55443661/.artifacts/f1ff4f8c-2bd2-4aec-862c-df50c0d88d83/decisiones_diseño.artifact.md)
- Documentación de las decisiones tomadas durante la refactorización.
