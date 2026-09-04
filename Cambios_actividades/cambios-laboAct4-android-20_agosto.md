# Resumen de Actividad: Semana 4 - State & Navigation

Este documento detalla la implementación de la gestión de estado y navegación en el proyecto de Actividades Formativas.

## 1. Archivos Creados
*   `ui/FormularioActividadUiState.kt`: Modelo de datos para el estado del formulario.
*   `ui/screens/FormularioActividad.kt`: Componente stateless para la entrada de datos.
*   `ui/screens/DetalleActividad.kt`: Pantalla para visualizar la información completa de una actividad.
*   `docs/semana_4_state_navigation.md`: Este archivo de documentación.

## 2. Archivos Modificados
*   `domain/ActividadFormativa.kt`: Se reincorporó el campo `prioridad` para cumplir con los requisitos del formulario.
*   `domain/ActividadRepository.kt`: Actualización de los datos iniciales con prioridades.
*   `MainActivity.kt`: Implementación del `NavHost`, elevación del estado y lógica de validación.
*   `build.gradle.kts` & `libs.versions.toml`: Adición de la dependencia de Navigation Compose.

## 3. Implementación General
Se ha transformado la aplicación de una sola pantalla con diálogos a una aplicación multi-pantalla con navegación formal. Se ha implementado un flujo completo de CRUD (Crear, Leer, Borrar) integrado con el diseño ágil existente.

## 4. Elevación del Estado (State Hoisting)
El estado del formulario se ha elevado desde el componente visual (`FormularioActividad`) hacia un contenedor superior en `MainActivity` llamado `FormularioScreen`. 
*   Se utilizó `rememberSaveable` para conservar el borrador del formulario (título, descripción, etc.) ante cambios de configuración o recreaciones de la UI.
*   El componente visual recibe un `UiState` inmutable y comunica los cambios mediante callbacks, siguiendo el patrón unidireccional de datos (UDF).

## 5. Navegación
Se configuró un `NavHost` con las siguientes rutas:
1.  `activities`: Pantalla principal (Lista con pestañas Ágiles).
2.  `activities/form`: Pantalla completa para el registro de nuevas actividades.
3.  `activities/{id}`: Pantalla de detalle que resuelve el objeto desde la fuente de verdad usando el ID.

## 6. Validaciones
Las reglas se implementaron en la función `buildUiState`:
*   **Título**: Obligatorio, longitud entre 3 y 80 caracteres.
*   **Descripción**: Opcional, máximo 240 caracteres.
*   **Fecha**: Formato `dd/mm/aaaa`, obligatoria y no puede ser anterior a la fecha actual del sistema.
*   **Progreso**: Validado como número entero entre 0 y 100.
*   **Botón Guardar**: Solo se habilita si todos los campos pasan las validaciones (cuando `errores` está vacío).

## 7. Prevención de Doble Guardado
Se implementó una protección doble:
1.  Un estado booleano `guardando` en el `UiState` que deshabilita el botón inmediatamente tras el primer clic.
2.  Una pequeña demora simulada (`delay`) en una corrutina para asegurar que la operación termine y la navegación ocurra antes de permitir otra interacción.

## 8. Pruebas Realizadas
*   **Navegación**: Verificación de entrada y salida de todas las pantallas (Backstack correcto).
*   **Validación de campos**: Se probaron casos de borde (títulos cortos/largos, fechas pasadas, progreso > 100) confirmando que los mensajes de error aparecen y el botón se bloquea.
*   **Persistencia temporal**: El borrador del formulario sobrevive al girar la pantalla gracias a `rememberSaveable`.
*   **ID inexistente**: Al navegar manualmente o por error a un ID que no existe, la pantalla de detalle muestra un mensaje amigable en lugar de cerrarse.

## 9. Intervención Manual
No se requiere intervención manual adicional. El proyecto compila y ejecuta todas las rutas correctamente.
