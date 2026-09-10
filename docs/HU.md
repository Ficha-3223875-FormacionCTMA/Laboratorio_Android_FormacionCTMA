# Historias de Usuario - Laboratorio Android CTMA

Documento centralizado de Historias de Usuario (HUs), Criterios de Aceptación, Riesgos relacionados y Casos de Prueba para la aplicación de gestión de actividades y tareas.

---

## **HU-01 - Gestionar estado de actividades con SnapshotStateList y Composables Stateless**

**Como** desarrollador,  
**quiero** que el estado de actividades esté en un `SnapshotStateList` y que los composables sean *stateless*,  
**para** facilitar la recomposición y simplificar la ejecución de pruebas unitarias y de UI.

---

#### **Criterios de Aceptación**

* **CA-01.1:** Mantener la lista principal de actividades estructurada en un `mutableStateListOf` / `SnapshotStateList` a nivel superior (p. ej., `MainApp` o `MainActivity`).
* **CA-01.2:** Garantizar que los componentes Composables reciban únicamente un `UiState` inmutable y *callbacks* para el manejo de eventos (aplicación estricta de *State Hoisting*).
* **CA-01.3:** Comprobar que las modificaciones y actualizaciones a la lista provoquen la recomposición correcta de la interfaz gráfica sin depender de referencias a listas estáticas.

---

#### **Riesgos relacionados**
* **R-01**
* **R-02**

#### **Casos de prueba relacionados**
* **TC-01** - Verificación de declaración de `SnapshotStateList` a nivel superior
* **TC-02** - Inspección de desacoplamiento y *State Hoisting* en componentes Composables
* **TC-03** - Prueba de recomposición dinámica de UI ante cambios en la lista

---

## **HU-02 - Crear y priorizar actividades mediante formulario flotante**

**Como** usuario,  
**quiero** poder crear una actividad para registrar tareas/entregas y priorizarlas,  
**para** gestionar mis pendientes de formación de manera organizada.

---

#### **Criterios de Aceptación**

* **CA-02.1:** Incluir un botón de acción flotante (*FAB* `+`) visible en la pantalla principal de Actividades.
* **CA-02.2:** Al pulsar el *FAB*, desplegar un formulario/diálogo con los campos: Título (obligatorio), Descripción (opcional) y Prioridad (`ALTA`, `MEDIA`, `BAJA`).
* **CA-02.3:** Validar el campo Título asignando un rango obligatorio de 3 a 80 caracteres; si es inválido, mostrar mensaje de error explicativo y bloquear el guardado.
* **CA-02.4:** Permitir la selección de Prioridad mediante componentes `FilterChip` y asociar el valor elegido al objeto de la actividad.
* **CA-02.5:** Al confirmar el guardado, generar un ID único para la actividad e insertarla inmediatamente en la lista visible.
* **CA-02.6:** Garantizar el bloqueo de acciones simultáneas para prevenir la creación de registros duplicados por doble pulsación rápida.

---

#### **Riesgos relacionados**
* **R-03**
* **R-04**

#### **Casos de prueba relacionados**
* **TC-04** - Verificación de despliegue de formulario/diálogo desde el FAB
* **TC-05** - Validación de rango de caracteres en el Título (3 a 80 caracteres)
* **TC-06** - Selección de prioridad mediante componentes FilterChip
* **TC-07** - Generación de ID único e inserción inmediata en la lista
* **TC-08** - Prevención de registros duplicados por doble clic

---

## **HU-03 - Eliminar actividad de la lista**

**Como** usuario,  
**quiero** poder eliminar una actividad para quitarla de mi lista cuando ya no sea necesaria,  
**para** mantener mi panel de tareas actualizado y limpio.

---

#### **Criterios de Aceptación**

* **CA-03.1:** Mostrar un botón "X" o de eliminación en cada tarjeta de actividad (`TarjetaActividad`).
* **CA-03.2:** Al confirmar la acción, remover la actividad del `SnapshotStateList` y recomponer la UI sin el elemento.
* **CA-03.3:** Asegurar que no queden residuos ni referencias de la actividad eliminada en la lista en memoria.
* **CA-03.4:** Garantizar que la eliminación sea inmediata, consistente e ininterrumpida ante acciones o eliminaciones consecutivas.

---

#### **Riesgos relacionados**
* **R-05**
* **R-06**

#### **Casos de prueba relacionados**
* **TC-09** - Visibilidad y presencia del botón de eliminación "X" en la tarjeta
* **TC-10** - Remoción de la actividad del `SnapshotStateList` y recomposición de UI
* **TC-11** - Verificación de ausencia de referencias residuales en memoria
* **TC-12** - Prueba de eliminaciones consecutivas sin fallos de índice

---

## **HU-04 - Editar detalles de una actividad existente**

**Como** usuario,  
**quiero** editar una actividad existente para modificar título, descripción, prioridad y fecha,  
**para** mantener actualizada la información de mis entregas a medida que cambian sus requerimientos.

---

#### **Criterios de Aceptación**

* **CA-04.1:** Incluir en cada `TarjetaActividad` un botón "E" o acción de editar que despliegue el formulario en modo edición.
* **CA-04.2:** Precargar en el formulario los valores existentes de la actividad seleccionada para su modificación.
* **CA-04.3:** Al confirmar el guardado, actualizar la actividad en la fuente de verdad y reflejar los cambios en la UI, manteniendo el orden por prioridad.
* **CA-04.4:** Aplicar las mismas reglas de validación en modo edición (título entre 3 y 80 caracteres, fecha válida y progreso).

---

#### **Riesgos relacionados**
* **R-07**
* **R-08**

#### **Casos de prueba relacionados**
* **TC-13** - Presencia y acción del botón "E" para abrir modo edición
* **TC-14** - Carga correcta de los datos existentes en los campos del formulario
* **TC-15** - Actualización en fuente de verdad y reordenamiento por prioridad en UI
* **TC-16** - Ejecución de validaciones de campos en modo edición

---

## **HU-05 - Marcar o desmarcar actividad como completada**

**Como** usuario,  
**quiero** marcar/desmarcar una actividad como completada para actualizar su progreso y estado visual,  
**para** llevar un control claro de las tareas que ya he finalizado.

---

#### **Criterios de Aceptación**

* **CA-05.1:** Incluir un componente *Checkbox* junto al título en cada tarjeta de actividad.
* **CA-05.2:** Al marcar el *Checkbox*, actualizar automáticamente el progreso de la actividad al 100% y refrescar la barra/indicador visual de la UI.
* **CA-05.3:** Al desmarcar el *Checkbox*, restaurar el progreso de la actividad al 0% y reflejar el cambio inmediatamente en la UI.
* **CA-05.4:** Garantizar que la modificación de estado se guarde en la fuente de verdad y persista durante la sesión actual del usuario.

---

#### **Riesgos relacionados**
* **R-09**
* **R-10**

#### **Casos de prueba relacionados**
* **TC-17** - Presencia y estado inicial del Checkbox junto al título
* **TC-18** - Actualización del progreso al 100% e indicador visual al marcar el Checkbox
* **TC-19** - Restablecimiento del progreso al 0% e indicador visual al desmarcar el Checkbox
* **TC-20** - Persistencia del estado completado en la fuente de verdad durante la sesión

---

## **HU-06 - Visualizar pantalla de detalle de una actividad**

**Como** usuario,  
**quiero** abrir una pantalla de detalle para ver título, descripción, prioridad, fecha, progreso y acciones sobre la actividad,  
**para** consultar toda su información técnica y opciones en una vista dedicada.

---

#### **Criterios de Aceptación**

* **CA-06.1:** Configurar la ruta de navegación `activities/{id}` para desplegar la información completa de la actividad seleccionada.
* **CA-06.2:** Si el `id` existe en la fuente de verdad, mostrar la totalidad de los atributos: título, descripción, prioridad, fecha y barra de progreso.
* **CA-06.3:** Si el `id` no existe o es inválido, presentar una pantalla de estado recuperable con un mensaje amigable al usuario en lugar de provocar un error o cierre abrupto (*crash*).
* **CA-06.4:** Incluir una opción o botón visible para regresar fluidamente al listado principal de actividades.

---

#### **Riesgos relacionados**
* **R-11**
* **R-12**

#### **Casos de prueba relacionados**
* **TC-21** - Verificación de la ruta parametrizada `activities/{id}`
* **TC-22** - Despliegue completo de la información de una actividad existente
* **TC-23** - Manejo de ID inexistente mediante pantalla de error amigable
* **TC-24** - Funcionalidad del botón de retorno al listado principal

---

## **HU-07 - Implementar flujo de navegación entre pantallas del CRUD**

**Como** usuario,  
**quiero** navegar entre pantallas (Lista $\rightarrow$ Crear/Editar $\rightarrow$ Detalle) para realizar flujos CRUD completos,  
**para** interactuar con la aplicación de forma fluida e intuitiva.

---

#### **Criterios de Aceptación**

* **CA-07.1:** Implementar un `NavHost` centralizado con las rutas estructuradas: `activities`, `activities/form` y `activities/{id}`.
* **CA-07.2:** Asegurar que la navegación conserve correctamente la pila de retroceso (*back stack*), permitiendo que el botón nativo o de la interfaz regrese al destino previo.
* **CA-07.3:** Transferir identificadores (`id`) entre los diferentes destinos, garantizando que cada pantalla resuelva los datos actualizados desde la fuente de verdad.
* **CA-07.4:** Permitir que el formulario de creación/edición se despliegue de forma flexible tanto en una pantalla dedicada como en un diálogo flotante según la ruta invocada.

---

#### **Riesgos relacionados**
* **R-13**
* **R-14**

#### **Casos de prueba relacionados**
* **TC-25** - Verificación de configuración y resolución de rutas en el `NavHost`
* **TC-26** - Validación del comportamiento del *back stack* al navegar entre vistas
* **TC-27** - Transferencia y resolución correcta de argumentos `id` entre pantallas
* **TC-28** - Despliegue del formulario según el modo de entrada (pantalla o diálogo)

---

## **HU-08 - Validar campos del formulario de creación/edición de actividades**

**Como** usuario,  
**quiero** que el formulario valide título, descripción, fecha y progreso,  
**para** asegurar que solo se guarden actividades válidas y con datos consistentes.

---

#### **Criterios de Aceptación**

* **CA-08.1:** Validar el campo Título como obligatorio, con una longitud de entre 3 y 80 caracteres.
* **CA-08.2:** Validar el campo Descripción como opcional, restringiendo su tamaño a un máximo de 240 caracteres.
* **CA-08.3:** Validar la Fecha en formato obligatorio `dd/mm/aaaa`, restringiendo el ingreso de fechas anteriores a la fecha actual del sistema.
* **CA-08.4:** Validar que el Progreso sea un número entero comprendido en el rango de 0 a 100.
* **CA-08.5:** Mantener el botón "Guardar" deshabilitado mientras exista algún error de validación en el formulario.
* **CA-08.6:** Mostrar mensajes de error claros y específicos debajo de cada campo con entradas no válidas.

---

#### **Riesgos relacionados**
* **R-15**
* **R-16**

#### **Casos de prueba relacionados**
* **TC-29** - Validación de rango de caracteres en el Título (3 a 80 caracteres)
* **TC-30** - Validación de límite máximo en Descripción (máximo 240 caracteres)
* **TC-31** - Validación de formato de Fecha (`dd/mm/aaaa`) y restricción de fechas pasadas
* **TC-32** - Validación de rango numérico en el Progreso (0 a 100)
* **TC-33** - Estado del botón Guardar según presencia de errores de validación
* **TC-34** - Renderizado de mensajes de error dinámicos por campo

---

## **HU-09 - Persistir borrador del formulario ante cambios de configuración**

**Como** usuario,  
**quiero** que el borrador del formulario sobreviva a cambios de configuración,  
**para** no perder los datos ingresados al rotar el dispositivo o cambiar el tamaño de la ventana.

---

#### **Criterios de Aceptación**

* **CA-09.1:** Implementar `rememberSaveable` (o la elevación de estado en ViewModel) para almacenar los valores de los campos al recrear la `Activity`.
* **CA-09.2:** Comprobar que tras la rotación de pantalla o recreación de la vista, los campos de texto y selecciones conserven sus datos exactos.
* **CA-09.3:** Garantizar que el borrador no se limpie ni se pierda hasta que el usuario confirme el guardado o descarte explícitamente el formulario.

---

#### **Riesgos relacionados**
* **R-17**
* **R-18**

#### **Casos de prueba relacionados**
* **TC-35** - Verificación del uso de `rememberSaveable` o retención en ViewModel
* **TC-36** - Preservación de campos ingresados tras rotación de pantalla (cambio de configuración)
* **TC-37** - Retención del borrador hasta la confirmación o cancelación explícita del usuario

---

## **HU-10 - Prevenir doble guardado para evitar duplicados**

**Como** usuario,  
**quiero** que la app impida el doble guardado al procesar una solicitud,  
**para** evitar registros duplicados y operaciones inconsistentes en el sistema.

---

#### **Criterios de Aceptación**

* **CA-10.1:** Deshabilitar inmediatamente el botón "Guardar" al hacer clic, reflejando el estado booleano `guardando` en el `UiState`.
* **CA-10.2:** Aplicar protección adicional en la lógica del ViewModel (control de concurrencia o bloqueo de evento) para ignorar pulsaciones consecutivas en rápida sucesión.
* **CA-10.3:** Re-habilitar la interacción de la interfaz correctamente tras completar la operación y realizar la navegación al destino correspondiente.

---

#### **Riesgos relacionados**
* **R-19**
* **R-20**

#### **Casos de prueba relacionados**
* **TC-38** - Deshabilitación inmediata del botón Guardar mediante bandera `guardando`
* **TC-39** - Bloqueo de solicitudes duplicadas ante clics múltiples rápidos (Concurrencia)
* **TC-40** - Restablecimiento de la interacción tras finalizar el guardado y la navegación

---

## **HU-11 - Desplegar indicador de tiempo restante en la tarjeta de actividad**

**Como** usuario,  
**quiero** ver un indicador de tiempo restante en cada tarjeta,  
**para** saber rápidamente el estado de entrega y priorizar mis tareas pendientes.

---

#### **Criterios de Aceptación**

* **CA-11.1:** Mostrar en cada `TarjetaActividad` un texto dinámico con los valores `"Faltan X d"`, `"Hoy"` o `"Vencida"`, calculado según la fecha de entrega respecto a la fecha actual del sistema.
* **CA-11.2:** Garantizar la precisión del cálculo de días restantes en pruebas unitarias e instrumentadas (ej. fecha actual $\rightarrow$ `"Hoy"`; fecha anterior $\rightarrow$ `"Vencida"`; fecha posterior $\rightarrow$ `"Faltan N d"`).
* **CA-11.3:** Actualizar inmediatamente la etiqueta o indicador cuando la fecha de entrega de la actividad sea modificada mediante la edición.

---

#### **Riesgos relacionados**
* **R-21**
* **R-22**

#### **Casos de prueba relacionados**
* **TC-41** - Despliegue correcto del texto de tiempo restante en la tarjeta según la fecha
* **TC-42** - Pruebas unitarias de cálculo de fechas (Hoy, Vencida, Faltan N d)
* **TC-43** - Re-cálculo y actualización dinámica del indicador tras modificar la fecha

---

## **HU-12 - Diseñar tarjetas de actividad expandibles con claves estables**

**Como** usuario,  
**quiero** tarjetas que muestren información relevante en forma compacta y que puedan expandirse para ver detalle adicional,  
**para** escanear rápido mis tareas sin saturar la pantalla con texto excesivo.

---

#### **Criterios de Aceptación**

* **CA-12.1:** Presentar de forma predeterminada un resumen compacto en la `TarjetaActividad` que incluya título, prioridad e indicador de tiempo restante.
* **CA-12.2:** Implementar un comportamiento expandible al interactuar con la tarjeta que revele la descripción, fecha completa y acciones secundarias (editar, eliminar).
* **CA-12.3:** Asegurar el uso de claves estables (`key` por `id`) en el componente `LazyColumn` para preservar la identidad y el estado de animación al recomponer.

---

#### **Riesgos relacionados**
* **R-23**
* **R-24**

#### **Casos de prueba relacionados**
* **TC-44** - Visualización de la tarjeta en estado colapsado por defecto
* **TC-45** - Expansión y despliegue de detalles y acciones secundarias al hacer clic
* **TC-46** - Preservación del estado de expansión e identidad usando claves estables (`key`)

---

### **HU-13 - Persistencia local de reportes, categorías y preferencias de usuario**

**Como** aprendiz de SENA / usuario de ReportaCTMA,  
**quiero** que mis reportes registrados, categorías y preferencias de filtro se guarden localmente en el dispositivo,  
**para** que la información se conserve al cerrar/reiniciar la app y pueda operar sin conexión a internet de forma consistente.

---

#### **Criterios de Aceptación**

* **CA-13.1 (Persistencia tras reinicio):** Garantizar que los reportes, categorías y preferencias se conserven de manera íntegra al cerrar, matar el proceso o reabrir la aplicación.
* **CA-13.2 (Sincronización reactiva):** Actualizar automáticamente la interfaz de usuario ante altas, cambios o bajas de datos utilizando `Flow`, sin requerir recargas o tiradas manuales.
* **CA-13.3 (Evolución de esquema):** Asegurar que al migrar la base de datos de v1 a v2 (añadiendo el campo `resuelto`), los reportes previos no sufran pérdida de datos y el nuevo campo inicie por defecto en `false`.
* **CA-13.4 (Manejo de inconsistencias/ID inexistente):** Presentar una vista o estado de error/vacío controlado ante la consulta de un ID inexistente, evitando bloqueos o cierres inesperados (*crashes*).

---

#### **Riesgos relacionados**
* **R-25**
* **R-26**
* **R-27**

#### **Casos de prueba relacionados**
* **TC-47** - Verificación de persistencia de reportes y preferencias tras reinicio de la app
* **TC-48** - Reactividad de la UI mediante Flow ante operaciones CRUD sin recarga manual
* **TC-49** - Migración de esquema v1 a v2 (adición del campo `resuelto` por defecto en `false`)
* **TC-50** - Manejo de estado de error controlado ante la consulta de un ID inexistente

---

## **HU-14 - Gestión de estados reactivos y concurrencia avanzada**

**Como** desarrollador,  
**quiero** que el listado de reportes/actividades se gestione mediante estados reactivos (Cargando, Contenido, Vacío, Error) y se combine con preferencias de DataStore y búsquedas,  
**para** ofrecer una experiencia de usuario fluida, resiliente y libre de bloqueos.

---

#### **Criterios de Aceptación**

* **CA-14.1 (Estado Listado):** Representar el listado mediante un `StateFlow` que emita selladamente: `Cargando`, `Contenido` (con datos), `Vacio` o `Error`.
* **CA-14.2 (Operación Independiente):** Separar el estado de las operaciones de escritura (guardado/eliminación) en un `OperacionUiState` para no interrumpir la visualización del listado.
* **CA-14.3 (Combinación Reactiva):** Combinar los datos de Room, el orden de DataStore y el término de búsqueda en un solo flujo, cancelando búsquedas obsoletas mediante `flatMapLatest` o `mapLatest`.
* **CA-14.4 (Ciclo de Vida Seguro):** Recolectar los estados en la UI utilizando `collectAsStateWithLifecycle` para evitar desperdicio de recursos en segundo plano.
* **CA-14.5 (Accesibilidad de Estados):** Mostrar indicadores visuales claros con texto descriptivo para carga, estados vacíos con acciones de reintento y mensajes de error comprensibles.
* **CA-14.6 (Manejo de Errores):** Capturar excepciones en el `viewModelScope`, relanzando obligatoriamente `CancellationException` para permitir el funcionamiento correcto de las corrutinas.

---

#### **Riesgos relacionados**
* **R-28**
* **R-29**
* **R-30**

#### **Casos de prueba relacionados**
* **TC-51** - Verificación de transición Cargando -> Vacío
* **TC-52** - Prueba de actualización reactiva al insertar reporte
* **TC-53** - Restauración de filtros desde DataStore
* **TC-54** - Cancelación de búsquedas rápidas (concurrencia)
* **TC-55** - Manejo de error con reintento (resiliencia)
* **TC-56** - Cancelación de Job por ciclo de vida
* **TC-57** - Consistencia de estado ante rotación de pantalla
* **TC-58** - Ejecución de suite de pruebas unitarias reactivas
