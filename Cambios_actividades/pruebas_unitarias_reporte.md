# Reporte de Pruebas Unitarias - Historias de Usuario

Este documento resume la implementación de las pruebas unitarias automatizadas para validar el cumplimiento de las Historias de Usuario (HU) definidas para el proyecto.

## 1. Archivos creados
*   `app/src/test/java/com/example/labo_android_semana_02/HistoriasUsuarioTest.kt`: Contiene los 12 tests unitarios (uno por cada HU).

## 2. Archivos modificados
*   `app/src/main/java/com/example/labo_android_semana_02/domain/ReglasActividad.kt`: Se añadió la función `calcularTextoTiempoRestante` para permitir pruebas unitarias de la lógica de visualización de plazos.
*   `app/src/main/java/com/example/labo_android_semana_02/ui/components/TarjetaActividad.kt`: Se actualizó para utilizar la función centralizada de cálculo de tiempo.

## 3. Qué se implementó
Se implementó una suite de pruebas que utiliza **JUnit 4** para verificar la lógica de negocio, validaciones de formularios, gestión de estados y cálculos de fechas, mapeando cada test directamente a una HU del proyecto.

## 4. Cómo se elevó el estado
El estado se elevó utilizando el patrón **State Hoisting** en `MainActivity.kt`. Las pruebas validan este comportamiento al verificar que la función `buildUiState` encapsula toda la lógica de transformación de datos de entrada en un estado inmutable (`FormularioActividadUiState`) listo para ser consumido por componentes *stateless*.

## 5. Cómo funciona la navegación
La navegación utiliza **Jetpack Navigation Compose**. Las pruebas validan la lógica de soporte a la navegación, como la generación de IDs incrementales y la resolución de objetos `ActividadFormativa` a partir de un ID (argumento de ruta).

## 6. Cómo se implementaron las validaciones
Las validaciones se centralizaron en la función `buildUiState`. Se implementaron reglas para:
*   **Título**: Obligatorio, entre 3 y 80 caracteres.
*   **Descripción**: Máximo 240 caracteres.
*   **Fecha**: Formato válido y no anterior a la fecha actual.
*   **Progreso**: Numérico entre 0 y 100.
Las pruebas (`test_HU08_FormValidation_Logic`) confirman que se generen los mensajes de error correctos y se bloquee el guardado si las reglas no se cumplen.

## 7. Cómo se evita el doble guardado
Se utiliza una bandera `guardando` en el `UiState`. El test `test_HU10_DoubleSavePrevention_Flag` verifica que si esta bandera está activa, la lógica de la UI (representada por el estado de habilitación del botón) impida una segunda ejecución.

## 8. Qué pruebas realizaste
Se ejecutaron 12 pruebas unitarias:
1.  **HU-01**: Verificación de lógica de State Hoisting.
2.  **HU-02**: Validación de creación con prioridad.
3.  **HU-03**: Lógica de eliminación en lista mutable.
4.  **HU-04**: Lógica de edición de campos.
5.  **HU-05**: Lógica de actualización de progreso (Checkbox).
6.  **HU-06**: Resolución de actividad por ID (Detalle).
7.  **HU-07**: Manejo de IDs en flujo de navegación.
8.  **HU-08**: Suite completa de validaciones de formulario.
9.  **HU-09**: Reconstrucción de estado (Simulación de persistencia).
10. **HU-10**: Prevención de doble clic mediante flags de estado.
11. **HU-11**: Cálculo de tiempo restante (Hoy, Vencida, Faltan X d).
12. **HU-12**: Verificación de unicidad de IDs (Stable Keys).

## 9. Problemas encontrados
Se detectó que la lógica de visualización de plazos estaba directamente en el Composable de la tarjeta, lo que impedía su prueba unitaria pura. Se resolvió extrayendo dicha lógica a la capa de dominio (`ReglasActividad.kt`), mejorando la testabilidad y consistencia de la app.
