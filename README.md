# Laboratorio Android

Repositorio de laboratorio para proyectos Android escritos en Kotlin.

## Descripción
Proyecto de ejemplo / laboratorio Android hecho en Kotlin. Contiene la estructura básica de una app Android y ejemplos de configuración con Gradle para facilitar pruebas y experimentación.

## Tecnologías
- Kotlin (100%)
- Android SDK
- Gradle
- Android Studio

## Requisitos
- Android Studio (recomendado) o SDK + herramientas de línea de comandos
- JDK 11+ instalado
- Android SDK (API level apropiado para el proyecto)
- Dispositivo físico o emulador Android

## Instalación y ejecución
1. Clona el repositorio:
   git clone https://github.com/JuanManuelCM3331/Laboratorio_Android_repo.git
2. Abre el proyecto en Android Studio:
    - File → Open → selecciona la carpeta del proyecto
    - Deja que Android Studio sincronice Gradle y descargue dependencias
3. Ejecuta la app:
    - Ejecuta en un emulador o dispositivo conectado desde Android Studio (Run → Run 'app')
    - O por línea de comandos:
        - ./gradlew assembleDebug  # compila el APK
        - ./gradlew installDebug   # instala en dispositivo/emulador (si está conectado)

## Tests
- Ejecutar tests unitarios:
  ./gradlew test
- Ejecutar tests de instrumentación en dispositivo/emulador:
  ./gradlew connectedAndroidTest

## Estructura del proyecto
- /app — módulo principal de la aplicación
    - src/main/java — código fuente en Kotlin
    - src/main/res — recursos (layouts, drawables, valores)
    - build.gradle (module) — configuración del módulo
- build.gradle (root) — configuración raíz y dependencias comunes
- settings.gradle — configuración de módulos del proyecto

(Ajusta esta sección si tu proyecto tiene más módulos o carpetas)

## Buenas prácticas
- Mantener las dependencias actualizadas
- Usar `ViewModel` y `LiveData` o `Flow` para separar lógica de UI
- Escribir tests unitarios para la lógica crítica
- Agregar linters / formateadores (Ktlint, Detekt) según convenga

## Contribuciones
Si quieres contribuir:
1. Haz fork del repositorio.
2. Crea una branch feature/bugfix con un nombre descriptivo.
3. Envía un pull request explicando los cambios.