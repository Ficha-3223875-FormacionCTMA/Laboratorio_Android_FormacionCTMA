# Edición de Actividades y Tiempo Restante

Se ha mejorado la gestión de actividades permitiendo la edición de tareas existentes y optimizando la visualización de los plazos de entrega.

## Funcionalidades Implementadas

### 1. Tiempo Restante (Sustitución de Porcentaje)
- **Visualización**: Se eliminó el porcentaje estático de la barra inferior de cada actividad.
- **Indicador Dinámico**: Ahora se muestra el tiempo restante calculado en tiempo real:
    - **Faltan X d**: Días que restan para la entrega.
    - **Hoy**: La entrega es en el día en curso.
    - **Vencida**: La fecha de entrega ya ha pasado.
    - **OK**: Se muestra cuando la tarea ha sido marcada como completada.

### 2. Edición de Actividades (Update Completo)
- **Interfaz**: Se añadió un botón **"E"** (Editar) en color azul junto al botón de eliminar.
- **Flujo de Edición**: Al pulsar "E", se abre el diálogo de actividad pre-rellenado con la información actual.
- **Campos Modificables**:
    - Título.
    - Descripción.
    - Días para la entrega (se recalcula la fecha basándose en los días ingresados desde el momento de la edición).

## Mejoras Técnicas
- **Reutilización de Componentes**: El diálogo de creación se transformó en un `DialogActividad` polimórfico que maneja tanto la creación como la edición.
- **Cálculo de Plazos**: Se implementó una lógica de conversión de milisegundos a días para mostrar información comprensible al usuario.
