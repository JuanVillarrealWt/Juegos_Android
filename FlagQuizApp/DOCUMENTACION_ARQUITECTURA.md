# Documentación de Arquitectura - FlagQuiz (Clean Architecture)

Este documento detalla la refactorización arquitectónica realizada en el proyecto **FlagQuiz**, migrando desde una estructura plana tradicional hacia **Clean Architecture**, con el objetivo de desacoplar la lógica de negocio, mejorar la mantenibilidad y facilitar las pruebas unitarias y de integración.

---

## 1. Resumen de la Refactorización

- **Sin modificación de recursos ni assets**: Se respetaron íntegramente la carpeta `res` (layouts, valores, menús, animaciones) y la estructura de imágenes en `assets`.
- **Enfoque en capas**: El código fuente en Kotlin/Java fue reorganizado bajo los principios de Clean Architecture en el paquete base `fisei.uta.edu.ec.flagquiz`.

---

## 2. Nueva Estructura de Paquetes

La arquitectura se divide en capas concéntricas bien definidas:

```text
fisei.uta.edu.ec.flagquiz/
│
├── application/
│   ├── contracts/        # Interfaces y contratos (ej. QuizRepository)
│   └── usecases/         # Casos de uso / Lógica de negocio (ej. GetQuizQuestionsUseCase)
│
├── composition/          # Inyección de dependencias y contenedor (ej. AppContainer)
│
├── domain/
│   └── entities/         # Entidades de dominio puras (ej. Country)
│
├── infrastructure/
│   └── repositories/     # Implementaciones de datos y acceso externo (ej. AssetQuizRepository)
│
└── presentation/         # Interfaz de usuario (Activities, Fragments)
    ├── MainActivity.java
    ├── MainActivityFragment.java
    ├── SettingsActivity.java
    └── SettingsActivityFragment.java
```

---

## 3. Diagrama de Arquitectura y Flujo de Dependencias

El siguiente diagrama ilustra el flujo de dependencias entre las capas del proyecto. La **Regla de Dependencia** establece que las dependencias deben apuntar únicamente hacia adentro, hacia el Dominio y la Aplicación.

```text
+-----------------------------------------------------------------------+
|                         PRESENTATION LAYER                            |
|   (MainActivity, MainActivityFragment, SettingsActivity,              |
|    SettingsActivityFragment)                                          |
+-----------------------------------++----------------------------------+
                                    || Usa
                                    \/
+-----------------------------------------------------------------------+
|                         COMPOSITION LAYER                             |
|                          (AppContainer)                               |
+-----------------------------------++----------------------------------+
                                    || Inyecta
                                    \/
+-----------------------------------------------------------------------+
|                         APPLICATION LAYER                             |
|  - Contracts: QuizRepository                                          |
|  - Use Cases: GetQuizQuestionsUseCase                                 |
+-----------------------------------++----------------------------------+
                                    || Usa
                                    \/
+-----------------------------------------------------------------------+
|                           DOMAIN LAYER                                |
|                             (Country)                                 |
+-----------------------------------------------------------------------+
                                    /\
                                    || Implementa interfaces de contrato
+-----------------------------------++----------------------------------+
|                       INFRASTRUCTURE LAYER                            |
|                     (AssetQuizRepository)                             |
+-----------------------------------------------------------------------+
```

---

## 4. Detalle de Capas y Componentes

### A. Capa de Dominio (`domain.entities`)
- **Propósito**: Contiene los modelos de negocio fundamentales de la aplicación, completamente independientes de Android (sin dependencias de Frameworks, UI o bases de datos).
- **Clases principales**:
  - `Country`: Representa un país, su identificador de archivo (`fileName`), nombre formateado y región a la que pertenece.

### B. Capa de Aplicación (`application`)
- **Propósito**: Contiene las reglas de negocio de la aplicación y define contratos que el sistema debe cumplir.
- **Componentes**:
  - `contracts.QuizRepository`: Interfaz que define qué operaciones de datos necesita la aplicación (obtener lista de nombres de archivos por región, abrir streams de imágenes, formatear nombres de países).
  - `usecases.GetQuizQuestionsUseCase`: Caso de uso encargado de la selección aleatoria de las 10 banderas del quiz, evitando duplicados y barajando las opciones.

### C. Capa de Infraestructura (`infrastructure.repositories`)
- **Propósito**: Implementa los contratos definidos en la capa de aplicación interactuando con fuentes de datos externas o APIs del sistema operativo Android.
- **Componentes**:
  - `AssetQuizRepository`: Implementa `QuizRepository` utilizando el `AssetManager` de Android para leer las imágenes de los países organizadas por carpetas de regiones en los `assets`.

### D. Capa de Composición (`composition`)
- **Propósito**: Gestiona la creación de instancias y el cableado de dependencias (Dependency Injection / Service Locator manual).
- **Componentes**:
  - `AppContainer`: Fábrica centralizada que provee las instancias de repositorios y casos de uso a la capa de presentación.

### E. Capa de Presentación (`presentation`)
- **Propósito**: Maneja la interfaz de usuario, eventos de botones, ciclo de vida de actividades/fragmentos y preferencias del usuario (`SharedPreferences`).
- **Componentes**:
  - `MainActivity`: Actividad principal que gestiona el Toolbar, la orientación según el dispositivo (teléfono o tablet) y los cambios de preferencias.
  - `MainActivityFragment`: Fragmento principal del juego que interactúa con `GetQuizQuestionsUseCase` y `QuizRepository` para mostrar las banderas, verificar aciertos/intentos y mostrar resultados.
  - `SettingsActivity` y `SettingsActivityFragment`: Gestionan la configuración de preferencias del usuario (número de opciones y regiones a incluir).

---

## 5. ¿Por qué Clean Architecture en este proyecto?

1. **Separación de responsabilidades**: La lógica de selección de preguntas ya no está mezclada directamente en el fragmento de UI, sino delegada a los **Use Cases**.
2. **Desacoplamiento de datos**: El acceso a archivos mediante `AssetManager` está abstraído detrás de una interfaz (`QuizRepository`), permitiendo cambiar o simular la fuente de datos fácilmente.
3. **Mantenibilidad y Explicabilidad**: Ideal para proyectos académicos y profesionales donde el docente evaluador puede rastrear claramente el flujo: *UI (Presentation) -> UseCase (Application) -> Repository (Infrastructure) -> Entity (Domain)*.
