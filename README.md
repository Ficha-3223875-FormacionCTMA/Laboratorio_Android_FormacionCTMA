# ReportaCTMA — Mi Formación Profesional

Aplicación Android desarrollada como proyecto integrador del programa de formación **Desarrollo de aplicaciones móviles Android** (ADSO, SENA). ReportaCTMA permite registrar, consultar, editar, eliminar y sincronizar actividades formativas, manteniendo una experiencia **offline-first** mediante una base de datos local y una API REST remota.

> **Estado de esta documentación:** este README describe la implementación actual del repositorio y documenta, además, los controles recomendados para selección y manejo seguro de fotografías, notificaciones, configuración y secretos. En la implementación revisada el `AndroidManifest.xml` declara `INTERNET` y `ACCESS_NETWORK_STATE`; no declara permisos de galería ni `POST_NOTIFICATIONS` porque esas capacidades no forman parte del flujo actual.

---

## 📌 Resumen del sistema

ReportaCTMA está compuesto por una aplicación Android de una sola app/módulo (`app`) con tres responsabilidades principales:

1. **Gestión local de actividades y reportes:** altas, consultas, modificaciones, eliminación y cambio de estado.
2. **Presentación reactiva:** interfaz construida con Jetpack Compose, navegación entre pantallas y estados explícitos de carga, contenido, vacío, error y operación.
3. **Sincronización remota:** consulta de actividades mediante Retrofit/OkHttp, almacenamiento posterior en Room y conservación de la caché cuando la red falla.

La fuente de verdad de la experiencia de lectura es Room. La red se utiliza para refrescar o respaldar información, no para bloquear el uso básico de la aplicación.

### Objetivos funcionales

- Facilitar el seguimiento de actividades de formación.
- Mostrar progreso, prioridad y fecha de entrega.
- Permitir trabajar sin conexión.
- Conservar datos y preferencias entre reinicios.
- Manejar errores de red sin destruir la información local.
- Proporcionar una base preparada para autenticación y sincronización segura.

---

## 🚀 Funcionalidades principales

### Gestión integral de actividades — CRUD

- **Registro:** creación de actividades con título, descripción, prioridad, fecha de entrega, progreso y estado de resolución.
- **Edición:** modificación de los campos de una actividad existente.
- **Eliminación:** borrado permanente desde la lista.
- **Control de estado:** un `Checkbox` permite alternar entre actividad pendiente y resuelta; al marcarla, el progreso pasa a 100% y, al desmarcarla, vuelve a 0%.
- **Protección contra doble envío:** el botón de guardado se deshabilita durante la operación para evitar registros duplicados.
- **Validación de formularios:** se verifican longitud del título y descripción, fechas válidas y rangos de progreso antes de persistir.

### Inteligencia de negocio y experiencia de usuario

- **Prioridad automática por tiempo:** las actividades próximas a vencer se resaltan; las que tienen menos de tres días de plazo se identifican como urgentes.
- **Ordenamiento dinámico:** las pendientes se muestran por vencimiento próximo; las completadas se separan visualmente.
- **Estados temporales:** se representan carga, lista vacía, contenido, error recuperable y operación en progreso.
- **Tarjetas expandibles:** el estado compacto muestra el resumen; al interactuar se revelan descripción, fecha completa y acciones.
- **Búsqueda en tiempo real:** filtra por título o descripción y cancela búsquedas obsoletas con operadores como `mapLatest`/`flatMapLatest`.
- **Tema claro/oscuro:** la preferencia visual se conserva y se aplica desde un único `MaterialTheme`.
- **Navegación por identificadores:** las rutas de detalle reciben el ID de la actividad, evitando transportar objetos completos entre pantallas.

### Persistencia y sincronización

- **Offline-first:** las pantallas observan datos locales de Room mediante `Flow`.
- **Room:** almacena reportes y actividades, ejecuta consultas y soporta migraciones de esquema.
- **DataStore Preferences:** conserva orden, filtros y preferencias de interfaz.
- **Retrofit:** consulta el endpoint remoto `GET /api/actividades`.
- **OkHttp:** aplica timeouts de conexión/lectura y logging controlado.
- **Sincronización atómica:** una respuesta remota válida se guarda en Room de forma consistente; una respuesta inválida o un error de servidor no debe corromper la caché existente.
- **Resiliencia:** con red fallida y caché disponible se muestran los datos locales junto con un aviso; sin red ni caché se muestra un estado recuperable con reintento.

---

## 🏗️ Arquitectura del sistema

El proyecto aplica **Clean Architecture** y **MVVM**, con separación entre presentación, reglas de negocio y fuentes de datos.

```text
┌─────────────────────────────────────────────────────────────┐
│ UI / Presentation                                           │
│ Compose · Screens · Navigation · ViewModel · UiState        │
└──────────────────────────┬──────────────────────────────────┘
                           │ eventos / estados
┌──────────────────────────▼──────────────────────────────────┐
│ Domain                                                     │
│ modelos · reglas · casos de uso/contratos · validaciones   │
└──────────────────────────┬──────────────────────────────────┘
                           │ interfaces de repositorio
┌──────────────────────────▼──────────────────────────────────┐
│ Data                                                       │
│ repositorios · mappers · Room · DataStore · Retrofit       │
└───────────────┬──────────────────────────┬─────────────────┘
                │                          │
        ┌───────▼────────┐         ┌───────▼────────┐
        │ Room / SQLite  │         │ API REST       │
        │ fuente local   │         │ Retrofit/OkHttp│
        └────────────────┘         └────────────────┘
```

### 1. Capa UI / Presentation

Ubicación principal: `app/src/main/java/com/example/labo_android_semana_02/ui`.

Incluye:

- Pantalla de bienvenida: `PantallaBienvenida`.
- Listado local: `PantallaActividades`.
- Listado/sincronización remota: `PantallaActividadesRemotas`.
- Formulario de alta y edición: `FormularioActividad`.
- Detalle: `DetalleActividad`.
- Componentes reutilizables, tarjetas, filtros, diálogos y tema visual.
- Composables preferentemente **stateless**, que reciben estado y emiten eventos mediante lambdas.
- `StateFlow`/`SharedFlow` para exponer estado y eventos de una sola vez.
- `collectAsStateWithLifecycle` para detener la recolección cuando la pantalla no está activa.
- `rememberSaveable` o ViewModel para conservar borradores y estados ante recreaciones.

La UI no debería ejecutar SQL, construir solicitudes HTTP ni contener reglas de negocio complejas. Su responsabilidad es representar el estado y enviar intenciones del usuario.

### 2. Capa Domain

Contiene modelos independientes de Android y de las implementaciones concretas:

- `ActividadFormativa` y `Reporte` como modelos de negocio.
- Enumeraciones como `Prioridad`.
- Reglas de fechas, vencimiento, progreso y clasificación de urgencia (`ReglasActividad.kt`).
- Contratos de repositorio, por ejemplo `ReporteRepository` y `PreferenciasRepository`.
- Validaciones reutilizables y funciones puras, fáciles de probar.

Esta capa define **qué** necesita el sistema, no **cómo** se consulta Room o la API.

### 3. Capa Data

Responsable de implementar los contratos del dominio y coordinar fuentes locales y remotas.

- **Room:** `AppDatabase`, DAOs, entidades y migraciones.
- **Repositorios:** `RoomReporteRepository` y `OfflineActividadRepository`.
- **DataStore:** `DataStorePreferenciasRepository` para orden, filtros y tema.
- **Retrofit:** `ActividadApi` expone `suspend fun getActividades(...)` y consulta `api/actividades`.
- **Remote data source:** `RemoteActividadDataSource` encapsula la comunicación remota.
- **Autenticación:** `SessionTokenProvider` suministra el token sin hardcodearlo en la interfaz de red.
- **DTOs y mappers:** convierten respuestas JSON en entidades locales y modelos de dominio.
- **Transacciones:** evitan reemplazos parciales y protegen la integridad del caché.

### Composición de dependencias

`MainActivity` crea y conecta las dependencias principales:

1. Construye `AppDatabase` con Room.
2. Registra migraciones, entre ellas `MIGRATION_1_2` y `MIGRATION_3_4`.
3. Configura `Json` con `ignoreUnknownKeys = true`.
4. Configura `HttpLoggingInterceptor` con redacción del encabezado `Authorization`.
5. Configura `OkHttpClient` con timeouts de 15 segundos.
6. Crea Retrofit y `ActividadApi`.
7. Construye la fuente remota y los repositorios.
8. Crea el `NavController` y el grafo de navegación Compose.
9. Proporciona repositorios y preferencias a las pantallas/ViewModels.

Para una evolución posterior se recomienda mover esta composición a un contenedor de dependencias dedicado o utilizar una librería de inyección, evitando que `MainActivity` concentre demasiadas responsabilidades.

---

## 🗂️ Estructura del repositorio

```text
.
├── app/
│   ├── schemas/                         # Esquemas exportados de Room
│   └── src/main/
│       ├── AndroidManifest.xml          # Permisos y configuración Android
│       └── java/com/example/.../
│           ├── MainActivity.kt          # Composición inicial y navegación
│           ├── data/
│           │   ├── local/               # Room, DAOs, entidades y DataStore
│           │   ├── remote/              # Retrofit, API, DTOs y token provider
│           │   └── repository/          # Implementaciones de repositorios
│           ├── domain/                  # Modelos, reglas y contratos
│           └── ui/                      # Compose, pantallas, estados y tema
├── Cambios_actividades/
│   └── contrato_api.md                  # Contrato JSON y códigos HTTP
├── docs/
│   ├── HU.md                            # Historias de usuario y aceptación
│   ├── PLAN_PRUEBAS.md                  # TC-01 a TC-64
│   └── RIESGOS.md                       # Riesgos y mitigaciones
├── build.gradle.kts                     # Configuración raíz Gradle
├── settings.gradle.kts
└── README.md
```

---

## 🔌 Contrato de integración remota

Endpoint documentado: `GET /api/actividades`.

Ejemplo de respuesta:

```json
[
  {
    "id": 1,
    "titulo": "Implementar Retrofit",
    "descripcion": "Configurar cliente y servicios remotos",
    "fecha_entrega": 1726000000000,
    "progreso": 45,
    "resuelto": false
  }
]
```

| Código | Significado | Tratamiento esperado |
|---|---|---|
| `200` | Lista válida, incluso `[]` | Persistir de forma atómica y actualizar la UI. |
| `401` | Token inválido o expirado | Solicitar renovación/inicio de sesión sin mostrar ni registrar el token. |
| `404` | Recurso no encontrado | Mostrar error recuperable y conservar caché si existe. |
| `500` | Error interno | Mostrar fallo técnico y mantener datos locales. |
| Timeout/sin red | No hay respuesta | Mostrar caché con aviso o estado de reintento si no hay caché. |

Campos principales del DTO:

- `id: Int`: identificador único.
- `titulo: String`: obligatorio.
- `descripcion: String?`: opcional.
- `fecha_entrega: Long`: timestamp en milisegundos.
- `progreso: Int`: rango de 0 a 100.
- `resuelto: Boolean`: estado de finalización.

---

## 🛠️ Stack tecnológico

- **Kotlin:** 2.0.21, motor K2.
- **Android:** `minSdk 24`, `targetSdk 37`, `compileSdk 37`.
- **UI:** Jetpack Compose y Material 3.
- **Navegación:** Navigation Compose.
- **Estado y ciclo de vida:** Coroutines, Flow, StateFlow, SharedFlow, Lifecycle Compose.
- **Persistencia:** Room 2.6.1 sobre SQLite y DataStore Preferences.
- **Red:** Retrofit 2.11.0, OkHttp y logging interceptor.
- **Serialización:** Kotlinx Serialization.
- **Procesamiento de anotaciones:** KSP.
- **Pruebas:** JUnit, Coroutines Test, Compose UI Test, Espresso, Room Testing.
- **Java:** compatibilidad de compilación y destino Java 11.

---

## 🧪 Calidad, pruebas y migraciones

La estrategia está documentada en `docs/PLAN_PRUEBAS.md` e incluye 64 casos (`TC-01` a `TC-64`) asociados a las historias de usuario.

- **HU-01 a HU-12:** UI, State Hoisting, CRUD, navegación, validaciones, fechas y tarjetas expandibles.
- **HU-13:** persistencia, reactividad, DataStore y migración Room.
- **HU-14:** estados reactivos, concurrencia, cancelación y ciclo de vida.
- **HU-15:** Retrofit, caché, timeout, respuestas vacías, `401`, `500` y refrescos concurrentes.
- **HU-16:** alternancia entre modo claro y oscuro.

La migración debe conservar datos existentes. `fallbackToDestructiveMigration()` está presente como mecanismo de respaldo de desarrollo, pero en producción debe preferirse una migración explícita para no perder información.

Comandos habituales:

```bash
./gradlew test
./gradlew connectedAndroidTest
./gradlew assembleDebug
```

---

## 🔐 Seguridad, privacidad y manejo de fotografías

### 1. Explique mínimo privilegio y aplíquelo a la selección de una fotografía

El **principio de mínimo privilegio** indica que una aplicación solo debe solicitar los permisos, datos y capacidades estrictamente necesarios para cumplir una acción, durante el menor tiempo posible y con el menor alcance posible.

Aplicado a la selección de una fotografía:

- Preferir **Photo Picker** (`PickVisualMedia`) para que la persona elija una imagen concreta.
- No solicitar acceso permanente a toda la galería si solo se necesita una fotografía.
- No pedir cámara si la funcionalidad únicamente selecciona una imagen existente.
- Validar y leer el `Uri` seleccionado solo durante la operación necesaria.
- Copiar a almacenamiento privado únicamente si el sistema requiere conservar la evidencia.
- Liberar permisos temporales o persistidos cuando ya no sean necesarios.

### 2. ¿Qué diferencia existe entre `content URI`, `file URI` y bytes de una imagen?

| Representación | Qué es | Implicaciones |
|---|---|---|
| **Content URI** (`content://...`) | Identificador abstracto administrado por un `ContentProvider`, por ejemplo Photo Picker o MediaStore. | No revela necesariamente una ruta física. Se accede mediante `ContentResolver`, se puede leer como stream y el permiso puede ser temporal. |
| **File URI** (`file://...`) | Referencia a una ruta del sistema de archivos. | Expone una ubicación concreta; compartirla entre apps está restringido en Android moderno y puede producir `FileUriExposedException`. Para compartir archivos se debe usar `FileProvider` y un `content://`. |
| **Bytes** (`ByteArray`) | Contenido binario de la imagen ya cargado en memoria. | No es una referencia ni un permiso; es el contenido. Puede enviarse en una petición o escribirse en un archivo, pero aumenta el uso de memoria y debe controlarse su tamaño. |

Un `content URI` puede transformarse en bytes leyendo el stream; no debe asumirse que su texto es una ruta de archivo local.

### 3. ¿Por qué Photo Picker reduce el alcance de permisos frente a leer toda la galería?

Photo Picker actúa como selector del sistema. La aplicación recibe acceso al elemento elegido, no un permiso general para enumerar y leer toda la biblioteca multimedia. Por eso reduce la superficie de exposición, limita la cantidad de información accesible y evita solicitar permisos amplios como leer imágenes de almacenamiento cuando no son necesarios.

También mejora la privacidad porque la persona decide explícitamente qué elemento comparte y la aplicación no necesita conocer el resto de la galería.

### 4. ¿Qué debe ocurrir cuando una persona niega `POST_NOTIFICATIONS`?

La aplicación debe continuar funcionando para las funciones que no dependen de notificaciones:

- No debe cerrarse ni entrar en un estado inválido.
- Debe registrar internamente que las notificaciones no están disponibles o tratar el resultado como permiso denegado.
- Debe evitar reintentos constantes o mensajes intrusivos.
- Las operaciones críticas no deben depender únicamente de una notificación.
- La UI puede mostrar una explicación contextual y ofrecer un acceso opcional a ajustes si las notificaciones son relevantes.
- Nunca se debe degradar el acceso a actividades, reportes o datos locales por esta negativa.

### 5. ¿Qué datos de una imagen deben validarse antes de persistirla o enviarla?

Como mínimo:

- Que el `Uri` sea válido y que el contenido pueda leerse.
- Tipo MIME real y extensión coherente (`image/jpeg`, `image/png`, `image/webp`, etc.).
- Tamaño total en bytes y límites de memoria/subida.
- Dimensiones: ancho, alto y megapíxeles máximos.
- Integridad del archivo: que pueda decodificarse correctamente.
- Orientación EXIF y metadatos que puedan revelar ubicación, dispositivo o información personal.
- Nombre de archivo sin rutas manipuladas ni caracteres peligrosos.
- Formato permitido y rechazo de contenido ejecutable disfrazado de imagen.
- Identificador de usuario/actividad y autorización para asociarla al registro correcto.
- Estado de red, hash o deduplicación si se requiere evitar cargas repetidas.

La validación debe hacerse antes de guardar en Room, escribir en almacenamiento privado o construir el cuerpo de la solicitud HTTP.

### 6. ¿Por qué la validación del cliente no reemplaza la validación del servidor?

El cliente está bajo control de la persona usuaria y puede ser modificado, descompilado o evadido. Un atacante puede enviar peticiones directamente al servidor sin usar la aplicación, alterar el APK o falsificar campos. Por ello, el servidor debe repetir las validaciones de tipo, tamaño, formato, autorización, propiedad del recurso y reglas de negocio.

La validación del cliente mejora la experiencia y evita errores tempranos; la del servidor es la que establece el límite de confianza y protege los datos compartidos.

### 7. Diferencie dato de configuración y secreto. Clasifique `API_BASE_URL` y un access token

- **Dato de configuración:** valor necesario para que la aplicación se conecte o se comporte correctamente, pero que no concede privilegios por sí mismo. Puede variar por ambiente y normalmente puede distribuirse con la aplicación.
- **Secreto:** credencial cuyo conocimiento permite autenticarse, autorizar operaciones o acceder a recursos protegidos. No debe estar hardcodeado ni aparecer en logs, repositorios o artefactos públicos.

Clasificación:

- `API_BASE_URL`: **configuración**, no es un secreto por sí sola. Debe separarse por ambiente (`debug`, `staging`, `release`) y no debe confundirse con una credencial.
- `access token`: **secreto/credencial temporal**. Debe obtenerse mediante un flujo seguro, almacenarse con protección adecuada, enviarse solo por HTTPS y nunca registrarse en logs.

### 8. Mencione tres controles para evitar exponer evidencias o credenciales en producción

1. **Secretos fuera del código y del repositorio:** usar variables seguras del entorno de CI/CD, secret managers o configuración protegida; rotar tokens y revisar el historial de Git.
2. **Logs y artefactos seguros:** desactivar `HttpLoggingInterceptor.Level.BODY` en release, redactar `Authorization`, no imprimir bytes/URIs sensibles y evitar subir evidencias, backups o APKs con datos reales.
3. **Control de acceso y protección de datos:** usar HTTPS, autenticación/autorización por recurso, almacenamiento privado/cifrado, reglas de retención y revisión de permisos antes de compartir archivos.

Otros controles recomendables son análisis de secretos en CI, `FileProvider` en lugar de `file://`, minimización de metadatos EXIF, expiración de URLs de descarga y auditoría de accesos.

---

## 🌐 Configuración actual y recomendaciones de producción

El cliente HTTP actual usa una URL simulada configurada en `MainActivity.kt`:

```kotlin
.baseUrl("https://TU-MOCK-SERVER.mock.pstmn.io/")
```

Para producción:

- Mover `API_BASE_URL` a configuración por variante de compilación o un mecanismo de configuración controlado.
- Mantener HTTPS y validar certificados según la política del proyecto.
- No incluir tokens reales en `BuildConfig`, recursos, logs ni commits.
- Desactivar logs de cuerpo y de cabeceras sensibles en `release`.
- Implementar renovación, expiración y revocación de tokens.
- Mantener respuestas remotas fuera de la UI hasta validar DTOs y autorización.
- Conservar la caché anterior ante respuestas parciales, inválidas o no autorizadas.

Permisos declarados actualmente en `AndroidManifest.xml`:

- `android.permission.INTERNET`.
- `android.permission.ACCESS_NETWORK_STATE`.

Si se incorpora selección de imágenes o notificaciones, se debe justificar cada permiso y solicitarlo solo cuando el flujo lo necesite.

---

## 📚 Documentación relacionada

- [Historias de usuario](docs/HU.md)
- [Plan de pruebas](docs/PLAN_PRUEBAS.md)
- [Matriz de riesgos](docs/RIESGOS.md)
- [Contrato de la API](Cambios_actividades/contrato_api.md)

---

## 🤖 Uso de inteligencia artificial

El desarrollo ha sido asistido por herramientas de IA para:

- Generación de código repetitivo y mappers.
- Optimización de consultas SQL y corrutinas.
- Refactorización arquitectónica.
- Elaboración y ampliación de casos de prueba.
- Revisión de documentación técnica y controles de seguridad.

Cada cambio generado debe revisarse, ajustarse y validarse manualmente. La responsabilidad final sobre el código, la seguridad, los datos y las decisiones de arquitectura corresponde al equipo de desarrollo.

---

## 📄 Licencia y contexto académico

Proyecto académico desarrollado en el marco de la formación ADSO del SENA. Antes de distribuirlo en producción se deben completar la gestión de secretos, autenticación real, observabilidad segura, política de privacidad, protección de datos y revisión de permisos.
