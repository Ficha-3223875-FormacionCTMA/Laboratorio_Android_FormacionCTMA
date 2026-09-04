# Entregable Laboratorio - Semana 4: Pruebas de API con Postman

Este documento detalla la actividad de laboratorio realizada para simular y probar los flujos de registro de actividades mediante Postman y un Mock Server.

## 1. Objetivo de la Actividad
Practicar pruebas de API en un entorno simulado para validar las reglas de negocio del proyecto Android (registro de actividades) antes de contar con un backend real.

## 2. Configuración del Entorno
Se han creado dos archivos JSON en la carpeta `docs/` para facilitar la reproducción de las pruebas:
- **Colección**: `postman_collection.json`
- **Ambiente**: `postman_environment.json`

### Instrucciones de uso:
1. Abrir Postman.
2. Importar ambos archivos (**File > Import**).
3. Seleccionar el ambiente "Laboratorio Android - Semana 4".
4. Configurar la variable `{{baseUrl}}` con la URL de tu Mock Server de Postman.

## 3. Resumen de Pruebas Realizadas

### Estructura de la Colección
- **Activities**: Casos de flujo principal y consultas.
- **Validation**: Casos de prueba negativos para validar restricciones.

### Casos de Prueba (8 Requests)
| ID | Escenario | Método | Status | Aserciones Principales |
|---|---|---|---|---|
| 1 | Registrar actividad válida | POST | 201 | Status 201, status "CREATED" |
| 2 | Registrar sin título | POST | 400 | Status 400, mensaje de error obligatorio |
| 3 | Símbolos en campo 'days' | POST | 400 | Status 400, validación de solo números |
| 4 | Valor negativo en 'days' | POST | 400 | Status 400, validación de número positivo |
| 5 | Descripción vacía | POST | 201 | Status 201, creación exitosa |
| 6 | Consultar existente | GET | 200 | Status 200, validación de ID |
| 7 | Consultar inexistente | GET | 404 | Status 404, error no encontrado |
| 8 | Eliminar actividad | DELETE | 200 | Status 200, mensaje de éxito |

## 4. Conclusiones
- Se validó que las reglas de negocio definidas en el proyecto Android (título obligatorio, días positivos) están correctamente modeladas para una futura integración con API.
- El uso del Mock Server permitió automatizar 16 aserciones (2 por request) sin depender de un servidor real.
- **Nota**: Toda la evidencia recolectada en este laboratorio corresponde a un entorno simulado (MOCK).
