# Contrato JSON de Actividades

Este documento describe la estructura de datos intercambiada con el servidor simulado para la gestión de actividades.

## Recurso: Actividades
**URL**: `/api/actividades`  
**Método**: `GET`

### Respuesta Exitosa (200 OK)
Retorna un arreglo de objetos de actividad.

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

### Códigos de Respuesta Previstos

| Código | Condición | Tratamiento en App |
|---|---|---|
| 200 | Lista de actividades (puede ser vacía `[]`) | Se guarda en Room y actualiza UI. |
| 401 | Token inválido o expirado | Se limpia sesión y pide login. |
| 404 | Recurso no encontrado | Se muestra error de "Servicio no disponible". |
| 500 | Error interno del servidor | Se muestra error técnico y conserva caché. |

## Estructura ActividadDto

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| id | Int | Sí | Identificador único |
| titulo | String | Sí | Nombre de la actividad |
| descripcion | String | No | Detalle (puede ser null/vacío) |
| fecha_entrega | Long | Sí | Timestamp en milisegundos |
| progreso | Int | Sí | Valor entre 0 y 100 |
| resuelto | Boolean | Sí | Estado de resolución |
