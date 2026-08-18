# Laboratorio Android — Mi Formación CTMA

Repositorio de laboratorio para proyectos Android escritos en Kotlin, desarrollado como caso integrador acumulativo de la ruta de formación **Desarrollo de aplicaciones móviles Android** (programa ADSO, SENA).

## Descripción

Proyecto Android hecho en Kotlin con **Jetpack Compose**. Contiene la estructura básica de una app Android y evoluciona semana a semana mediante el caso **Mi Formación CTMA**: una aplicación que permite a un aprendiz organizar actividades, compromisos y evidencias de su proceso formativo. Cada incremento se construye sobre el anterior, sin reiniciar el proyecto, y queda documentado en el historial de Git.

## Tecnologías

* Kotlin (100 %)
* Jetpack Compose (UI declarativa)
* Material 3
* Android SDK
* Gradle
* Android Studio

## Requisitos

* Android Studio (recomendado) o SDK + herramientas de línea de comandos
* JDK 11+ instalado
* Android SDK (API level apropiado para el proyecto)
* Dispositivo físico o emulador Android

## Instalación y ejecución

1. Clona el repositorio: `git clone https://github.com/JuanManuelCM3331/Laboratorio_Android_repo.git`
2. Abre el proyecto en Android Studio:
    * File → Open → selecciona la carpeta del proyecto
    * Deja que Android Studio sincronice Gradle y descargue dependencias
3. Ejecuta la app:
    * Ejecuta en un emulador o dispositivo conectado desde Android Studio (Run → Run 'app')
    * O por línea de comandos:
        * `./gradlew assembleDebug` — compila el APK
        * `./gradlew installDebug` — instala en dispositivo/emulador (si está conectado)

## Tests

* Ejecutar tests unitarios: `./gradlew test`
* Ejecutar tests de instrumentación en dispositivo/emulador: `./gradlew connectedAndroidTest`

## Estructura del proyecto

* `/app` — módulo principal de la aplicación
    * `src/main/java` — código fuente en Kotlin (incluye los paquetes `domain`/`model` con las entidades y reglas de negocio)
    * `src/main/res` — recursos (layouts, drawables, valores)
    * `build.gradle` (module) — configuración del módulo
* `build.gradle` (root) — configuración raíz y dependencias comunes
* `settings.gradle` — configuración de módulos del proyecto

*(Ajusta esta sección si tu proyecto tiene más módulos o carpetas)*

---

## Fundamentos teóricos del caso integrador

Esta sección recoge los conceptos, preguntas orientadoras y criterios trabajados en las guías de aprendizaje semanales, para que el contenido teórico quede trazado junto con el código.

### Semana 1 · Entorno, ecosistema y trazabilidad

**Meta:** configurar un ambiente Android reproducible y ejecutar una aplicación base en emulador o dispositivo físico.

**¿Qué significa desarrollar para Android?**
Construir software que se ejecuta dentro del modelo de aplicaciones, seguridad, recursos y ciclo de vida del sistema operativo Android. Una app real no es solo una pantalla: incluye configuración, código, recursos, permisos, datos, pruebas y un proceso de compilación.

| Alternativa | Fortaleza | Cuándo considerarla |
|---|---|---|
| Nativa Android | Acceso directo al ecosistema y experiencia específica de Android | Producto Android, funciones del dispositivo y máximo control |
| Multiplataforma | Comparte parte del código entre plataformas | Alcance con varias plataformas y equipo que domina el framework |
| PWA | Distribución web y bajo costo de entrada | Procesos centrados en web con acceso limitado o progresivo al dispositivo |

**Herramientas y conceptos esenciales**

* **Android Studio:** entorno integrado para crear, ejecutar, depurar y perfilar aplicaciones.
* **SDK:** herramientas y APIs necesarias para compilar contra Android.
* **API level:** versión del conjunto de APIs disponible; no equivale al nombre comercial de Android.
* **Emulador y AVD:** dispositivo virtual con configuración de hardware e imagen del sistema.
* **Gradle:** automatiza compilación, dependencias y variantes.
* **AndroidManifest.xml:** declara componentes, capacidades y permisos de la aplicación.
* **Jetpack Compose:** define la interfaz de forma declarativa mediante funciones Kotlin.

**Anatomía mínima del proyecto**

```
app/
├─ src/main/
│  ├─ java/.../MainActivity.kt   # Punto de entrada inicial
│  ├─ res/                        # Textos, imágenes, temas y otros recursos
│  └─ AndroidManifest.xml         # Declaraciones de la aplicación
├─ build.gradle.kts               # Configuración del módulo
└─ proguard-rules.pro             # Reglas de optimización/obfuscación
```

**Del código a la pantalla.** Compose trabaja de forma declarativa: se describe cómo debe verse la interfaz para un estado dado. Una función anotada con `@Composable` puede producir elementos visibles, y el parámetro que recibe la hace reutilizable.

**Git desde el primer día:** revisar `git status` antes de `git add .`, no confirmar `local.properties`, llaves, tokens ni archivos de firma, y hacer que cada commit represente un cambio coherente y explicable.

**Preguntas de comprensión trabajadas en la guía**

1. ¿Qué diferencia práctica hay entre una aplicación móvil y una página web?
2. ¿Qué función cumple un sistema operativo como Android?
3. ¿Qué es una variable? Ejemplo relacionado con una actividad formativa.
4. ¿Qué estructura usarías para decidir si una actividad está vencida?
5. ¿Qué resultado esperas de una lista que almacena actividades?
6. ¿Para qué sirve un sistema de control de versiones?
7. ¿Qué información nunca debería subirse a un repositorio público?
8. ¿Qué harías primero si una aplicación se cierra inesperadamente?
9. ¿Qué significa "probar" una aplicación?
10. ¿Qué riesgos de privacidad puede tener una app que almacena datos de aprendices?

**Glosario de la semana:** AVD, Gradle, Compose, Logcat.

---

### Semana 2 · Kotlin para resolver problemas móviles

**Meta:** aplicar tipos, operadores, control de flujo, funciones y colecciones de Kotlin en situaciones del caso integrador.

**Kotlin: conciso, tipado y orientado a la seguridad.** Kotlin es un lenguaje de tipado estático; la inferencia permite omitir el tipo cuando es evidente. Se prefiere `val` cuando una referencia no necesita reasignarse, y `var` únicamente cuando el estado realmente cambia.

**Condiciones como expresiones.** Un `when` puede producir directamente un valor, evitando cadenas `if/else` difíciles de leer:

```kotlin
fun estadoProgreso(progreso: Int): String = when {
    progreso !in 0..100 -> "Progreso inválido"
    progreso == 100 -> "Completada"
    progreso >= 70 -> "Avanzada"
    progreso > 0 -> "En proceso"
    else -> "Pendiente"
}
```

**Funciones pequeñas y comprobables.** Una función debe tener una intención clara, no depender de variables globales, validar datos en los límites del sistema y preferir retornar resultados en lugar de imprimir.

**Colecciones y operaciones expresivas.** `listOf` crea una lista de solo lectura; `mutableListOf` permite modificarla, pero la mutabilidad no debe elegirse por costumbre porque incrementa los estados posibles.

**Null safety: ausencia explícita.** Un dato opcional se declara con `?`, lo que obliga a tratar su ausencia.

| Operador | Uso | Riesgo o recomendación |
|---|---|---|
| `?.` | Acceso seguro si el valor existe | Úsalo cuando la ausencia sea válida |
| `?:` | Valor alternativo cuando hay `null` | El valor alternativo debe tener sentido de negocio |
| `let` | Ejecuta un bloque con valor no nulo | Evita anidamientos excesivos |
| `!!` | Afirma que no es `null` | Evítalo salvo garantía demostrable |

**Modelado con `data class`.** Ofrece igualdad por contenido, representación legible y `copy()`. Las invariantes del bloque `init` impiden construir objetos incoherentes:

```kotlin
data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val progreso: Int = 0,
    val enlaceEvidencia: String? = null
) {
    init {
        require(titulo.isNotBlank()) { "El título es obligatorio" }
        require(progreso in 0..100) { "El progreso debe estar entre 0 y 100" }
    }
    val estaCompletada: Boolean get() = progreso == 100
}
```

**Estados cerrados con `sealed class`.** Útil cuando el conjunto de estados está controlado; el compilador puede verificar que un `when` cubra todas las alternativas.

**Interfaces sin sobrearquitectura.** Crear una abstracción cuando exista una responsabilidad o variación real; una interfaz por cada clase no mejora automáticamente el diseño.

**Preguntas para validar el aprendizaje**

* ¿Por qué elegiste `val` o `var` en un dato específico?
* ¿Qué pasaría si la lista estuviera vacía?
* ¿Dónde podría aparecer `null` y cómo lo controlaste?
* ¿Por qué una regla de negocio no debería estar duplicada dentro del composable?

**Glosario de la semana:** inferencia, null safety, data class, sealed class, función pura.

---

### Semana 3 · Interfaces declarativas con Jetpack Compose

**Meta:** construir una pantalla declarativa, reutilizable, accesible y adaptable para consultar actividades de Mi Formación CTMA.

**De Kotlin a una UI declarativa.** Una UI declarativa describe el resultado visual esperado para unas entradas: un composable recibe datos y declara qué se muestra. Cuando cambia un estado observado, Compose programa la recomposición de las partes que lo leen.

| Concepto | No significa | Aplicación en CTMA |
|---|---|---|
| `@Composable` | Una clase `View` o una función que retorna un widget | Describe un encabezado, tarjeta o pantalla |
| Recomposición | Redibujar siempre toda la aplicación | Actualiza solo las partes que leen el valor modificado |
| Idempotencia | Que la UI nunca pueda cambiar | Mismas entradas producen la misma descripción, sin efectos laterales ocultos |
| Estado | Cualquier `var` global | Dato observable que influye en lo visible o interactivo |

**Modifier: el orden también programa.** `Modifier` cambia tamaño, posición, apariencia, semántica e interacción, y se encadena de izquierda a derecha, por lo que `padding` antes o después de `clickable` puede cambiar el área táctil.

**Layouts.** `Column` organiza verticalmente, `Row` horizontalmente y `Box` superpone o alinea. El layout no debe asumir que el texto siempre ocupa una línea.

**Material 3, tema, color y tipografía.** `MaterialTheme` expone `colorScheme`, `typography` y `shapes`. El objetivo es crear jerarquía, contraste, estados y consistencia, no "pintar todo del color institucional". El estado (pendiente/completada, por ejemplo) no debe comunicarse solo con color: siempre debe reforzarse con texto o iconografía.

**Imágenes e iconos con significado.** Una imagen informativa necesita descripción; una imagen decorativa debe excluirse de la semántica con `contentDescription = null`.

**LazyColumn y LazyVerticalGrid.** Los contenedores lazy componen y disponen contenido según necesidad; se debe usar una `key` estable (por ejemplo, el `id` del dominio) para preservar identidad cuando la colección cambia, en lugar del índice.

**Diseño adaptable.** Adaptar no es solo estirar: el ancho disponible puede cambiar por tablet, plegable, multiventana u orientación. En este incremento se usa un layout compacto (una columna) y uno ampliado (cuadrícula o dos paneles) según el espacio.

**Accesibilidad y UX (principios POUR)**

| Principio | Verificación práctica | Riesgo que evita |
|---|---|---|
| Perceptible | Contraste, texto escalable, información no dependiente solo del color | Contenido invisible o ambiguo |
| Operable | Objetivos táctiles suficientes, orden y acciones comprensibles | Toques fallidos y navegación confusa |
| Comprensible | Etiquetas claras, estados y mensajes orientados a la tarea | Carga cognitiva y errores |
| Robusto | Semántica correcta y prueba con TalkBack cuando sea posible | Lectura sin contexto por tecnología de asistencia |

**Preguntas de cierre metacognitivo**

* ¿Qué dato de la Semana 2 fue más difícil de convertir en una decisión visual y por qué?
* ¿Qué recomposición observaste y qué la provocó?
* ¿Qué barrera de accesibilidad detectaste antes de que la detectara un usuario?

**Glosario de la semana:** composable, Composition, recomposición, Modifier, lazy layout, semántica, diseño adaptable.

---

### Semana 4 · Estado, formularios y navegación

**Meta:** implementar un flujo Android multipantalla que gestione estado observable, eventos, validación de formulario, navegación y restauración básica.

**Estado y flujo unidireccional (UDF).** El estado baja hacia la interfaz y los eventos suben hacia quien puede decidir. Un composable de formulario debe ser *stateless*: recibe un `UiState` y callbacks específicos, sin manejar directamente datos globales.

**`remember` vs. `rememberSaveable`.** `remember` conserva un valor mientras la composición permanece viva; `rememberSaveable` sobrevive además a la recreación de la Activity (por ejemplo, al rotar el dispositivo), por lo que es la opción adecuada para conservar un borrador simple de formulario. No se deben guardar listas o entidades grandes en el `Bundle`.

**State hoisting.** Elevar el estado hacia el contenedor que lo posee, dejando que los composables hijos solo lo reciban y notifiquen eventos, mantiene una única fuente de verdad y evita duplicaciones.

**Validación de formularios.** Reglas trabajadas en el caso: título obligatorio (3–80 caracteres), descripción opcional (máximo 240 caracteres), fecha con formato válido y progreso entre 0 y 100. Los mensajes de error deben indicar qué corregir y no depender solo del color.

**Navigation Compose.** Se definen destinos (por ejemplo, Lista, Crear y Detalle) y se pasan identificadores (`actividadId`), no entidades completas, para que el destino resuelva el dato contra la fuente de verdad. Es importante comprobar el comportamiento del *back stack*: que "Atrás" regrese al destino esperado sin duplicar pantallas, y que un argumento inexistente produzca un estado controlado en lugar de un cierre abrupto.

**Ciclo de vida y restauración.** La app debe distinguir entre recomposición (Compose vuelve a describir la UI) y recreación (la Activity se destruye y se vuelve a crear, por ejemplo al rotar), y comprobar que el borrador del formulario se conserve en el segundo caso.

**Preguntas de autoevaluación**

* ¿Puedo señalar quién posee cada estado y quién solo lo presenta?
* ¿La aplicación mantiene el borrador cuando se recrea la Activity?
* ¿Cada error explica una acción de corrección?
* ¿Puedo dibujar el back stack después de Lista → Crear → Atrás?
* ¿Puedo cambiar una regla de validación sin buscar una solución externa?

**Glosario de la semana:** `remember`, `rememberSaveable`, state hoisting, UDF, back stack, ciclo de vida.

---

## Buenas prácticas

* Mantener las dependencias actualizadas.
* Usar `ViewModel` y `LiveData` o `Flow` para separar lógica de UI.
* Mantener las reglas de negocio (validaciones, cálculos de estado) fuera de los composables, en el paquete `domain`/`model`.
* Escribir tests unitarios para la lógica crítica.
* Agregar linters / formateadores (Ktlint, Detekt) según convenga.
* No comunicar estados únicamente por color; reforzar siempre con texto o iconografía.
* Usar claves (`key`) estables por `id` en `LazyColumn`/`LazyVerticalGrid`, nunca el índice.

## Contribuciones

Si quieres contribuir:

1. Haz fork del repositorio.
2. Crea una branch `feature/bugfix` con un nombre descriptivo.
3. Envía un pull request explicando los cambios.