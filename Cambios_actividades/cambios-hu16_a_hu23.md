# Cambios — Implementación de las HU-16 a HU-23

Este documento resume qué se tocó del proyecto para cumplir las historias de usuario HU-16 a HU-23, y por qué se tomó cada decisión.

---

## Resumen de archivos

| Archivo | Estado | HU |
| --- | --- | --- |
| `gradle/libs.versions.toml` | Modificado | HU-21, HU-22 |
| `app/build.gradle.kts` | Modificado | HU-21, HU-22 |
| `domain/Reporte.kt` | Modificado | HU-21 |
| `ui/ReporteViewModel.kt` | Modificado | HU-21 |
| `ui/FormularioActividadUiState.kt` | Modificado | HU-19 |
| `ui/UtilidadesListado.kt` | **Nuevo** | HU-17, HU-22 |
| `ui/components/TarjetaReporte.kt` | Modificado | HU-21, HU-22 |
| `ui/screens/PantallaActividades.kt` | Modificado | HU-16, HU-17, HU-18, HU-21, HU-22 |
| `ui/screens/FormularioActividad.kt` | Modificado | HU-19 |
| `ui/screens/PantallaBienvenida.kt` | **Nuevo** | HU-20 |
| `MainActivity.kt` | Modificado | HU-16, HU-19, HU-20, HU-21 |
| `androidTest/.../HistoriasUsuarioAvanzadasTest.kt` | **Nuevo** | HU-16 a HU-23 |
| `docs/HU.md`, `docs/RIESGOS.md` | Modificados | Documentación |

---

## HU-16 — Modo claro / modo oscuro

La preferencia de tema se elevó hasta `setContent` en `MainActivity`, que es el único punto donde se instancia `Labo_android_semana_02Theme`. Se guarda con `rememberSaveable`, así que sobrevive a rotaciones y a recomposiciones durante toda la sesión (CA-16.3). El valor inicial se toma de `isSystemInDarkTheme()`.

El control es un `Switch` ubicado en las `actions` de la `TopAppBar` del listado (CA-16.1), acompañado de un icono que cambia entre `LightMode` y `DarkMode`. Como el tema se aplica en la raíz, toda la app cambia de paleta usando los esquemas ya definidos en `Theme.kt` (CA-16.2); no se agregaron colores nuevos.

## HU-17 — Contador de pendientes vs. completados

Se creó el archivo `ui/UtilidadesListado.kt` con el modelo `ResumenListado` y la función pura `List<Reporte>.calcularResumen()`. La tarjeta `ResumenContadores` se dibuja arriba del listado y recibe los valores ya calculados.

Al derivarse de la lista vigente del `UiState`, los contadores se recalculan solos en cada emisión: agregar, eliminar o marcar/desmarcar un reporte actualiza el resumen sin lógica adicional (CA-17.2).

## HU-18 — Confirmación de borrado

`PantallaActividades` mantiene un estado `reporteAEliminar: Reporte?`. El botón de borrado de la tarjeta ya no elimina: solo llena ese estado, lo que despliega el `AlertDialog` (CA-18.1).

El diálogo muestra el mensaje exacto pedido y los botones "Cancelar" y "Confirmar" (CA-18.2). La llamada real a `onDeleteReporte` ocurre únicamente dentro del `confirmButton` (CA-18.3).

## HU-19 — Botón Limpiar

Se agregó la propiedad calculada `estaVacio` a `FormularioActividadUiState`: es `true` cuando título, descripción y fecha están en blanco. El `OutlinedButton` "Limpiar" usa `enabled = !uiState.estaVacio` (CA-19.3).

El callback `onLimpiar` se implementa en `FormularioScreen` (MainActivity) y restablece las cuatro variables de estado del borrador de una sola vez (CA-19.2).

## HU-20 — Pantalla de bienvenida

Nuevo composable sin estado `PantallaBienvenida`, registrado en el `NavHost` bajo la ruta `welcome`, que además pasó a ser el `startDestination` (CA-20.1). Muestra logo circular, el nombre "ReportaCTMA" y el botón "Ingresar" (CA-20.2).

La navegación usa `popUpTo("welcome") { inclusive = true }`, así que al presionar atrás desde el listado la app se cierra en vez de volver a la bienvenida (CA-20.3).

## HU-21 — Favoritos

Se agregó el campo `esFavorito: Boolean = false` al modelo de dominio `Reporte`. **La bandera no se persiste en Room a propósito**: vive en el `ViewModel` como un `MutableStateFlow<Set<Int>>` con los IDs destacados, que entra al `combine` junto con Room, DataStore y la búsqueda, y se proyecta sobre cada elemento antes de emitir el `UiState`.

Esto evita una migración de base de datos v2 → v3 y cumple lo que pide TC-82: el estado de favorito sobrevive a la recomposición porque su fuente de verdad es el ViewModel, no el composable. Los mappers no se tocaron.

En la tarjeta, un `IconButton` alterna entre `Icons.Default.Star` y `Icons.Default.StarBorder`, con tinte dorado (`0xFFFFC107`) cuando está marcado (CA-21.3).

## HU-22 — Copiar al portapapeles

El formato se centralizó en la función pura `formatoPortapapeles(reporte)` → `"#ID - Título"`, para poder probarla sin instrumentación de UI.

La copia usa `LocalClipboardManager` de Compose (que delega en el `ClipboardManager` de Android) y se confirma con un `Toast` corto con el texto "Copiado al portapapeles" (CA-22.2 y CA-22.3).

## HU-23 — Prueba integral

Se agregó `HistoriasUsuarioAvanzadasTest.kt` en `androidTest`, en archivo aparte para no chocar con la suite existente. Cubre TC-65 a TC-88 siguiendo el mismo enfoque de la suite anterior: se valida la lógica de negocio, el `UiState` y los flujos reactivos.

El test `test_HU23_TC87_FlujoE2E_...` recorre el ciclo completo con dobles de prueba: vacío → crear → listar → editar → completar → eliminar → vacío. El `TC-88` encadena veinte operaciones seguidas para verificar que no quedan estados intermedios inválidos ni excepciones.

Los criterios puramente visuales (color del icono, despliegue del diálogo en pantalla, elementos de marca de la bienvenida) se verificaron a mano en el emulador y se reportan en el Pull Request.

---

## Nota sobre dependencias

Se agregó `androidx.compose.material:material-icons-extended` al catálogo de versiones. Era necesario porque `StarBorder`, `ContentCopy`, `DarkMode`, `LightMode` y `Assignment` no vienen en `material-icons-core`, que es lo único que trae `material3` por defecto. La versión la resuelve el Compose BOM que ya estaba configurado, así que no se fijó ningún número a mano.
