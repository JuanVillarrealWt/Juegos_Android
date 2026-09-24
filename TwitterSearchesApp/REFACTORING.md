# Refactorización del proyecto

## Qué se cambió y por qué

Antes de la refactorización, `MainActivity` concentraba varias responsabilidades: configuraba la pantalla, respondía a los eventos del usuario, leía y escribía directamente en `SharedPreferences`, validaba los campos y obtenía las consultas asociadas a cada etiqueta. Por eso, un cambio en la persistencia podía obligar a modificar una clase que también contenía navegación, diálogos y lógica visual. Además, las reglas de guardar y eliminar quedaban ligadas a la pantalla que las invocaba.

El cambio separó el proyecto por responsabilidades y por módulos Gradle. `app` sigue siendo el módulo Android ejecutable y conserva `MainActivity` y `TwitterCompositionRoot`: la Activity continúa mostrando la interfaz y el composition root conecta los módulos. La lógica de las operaciones quedó en `application.usecases`; sus contratos están en `application.contracts`; el modelo `TaggedSearch` está en `domain.entities`; y el acceso a preferencias se dividió entre `infrastructure.persistence` y `infrastructure.repositories`. Los componentes de lista y sus recursos se agruparon en `presentation`.

También se separaron dos conceptos que antes estaban mezclados en el mismo acceso a preferencias. `SharedPreferencesSearchesDataSource` conoce las llamadas Android y el formato clave/valor; `SharedPreferencesSearchesRepository` convierte ese formato al contrato y a la entidad que usa la aplicación. `SearchesUseCases`, por su parte, valida y coordina las operaciones sin necesitar saber cómo se guardan. Esta dirección de dependencias permite reemplazar `SharedPreferences` por otra solución en infraestructura conservando las reglas y la interfaz pública de los casos de uso.

La separación busca reducir el acoplamiento y hacer más claro dónde modificar cada comportamiento: cambios de presentación se trabajan en `presentation` o `app`; cambios en validaciones y operaciones, en `application`; cambios al modelo, en `domain`; y cambios de almacenamiento, en `infrastructure`. También facilita probar la lógica de aplicación usando otra implementación del contrato, sin iniciar una Activity ni depender de almacenamiento Android.

Se conservaron el `applicationId`, el punto de entrada Android y los flujos visibles para listar, guardar, editar, eliminar, compartir y abrir búsquedas. La intención fue reorganizar cómo se implementan y conectan esas funciones, sin cambiar lo que el usuario puede hacer en la aplicación.

## Qué hace cada clase

| Clase y ubicación | Qué hace ahora | Por qué quedó así |
| --- | --- | --- |
| `MainActivity` — `app` | Es la pantalla principal y recibe las acciones del usuario. Configura Toolbar, campos de texto, FAB y RecyclerView; carga y ordena etiquetas; muestra u oculta el FAB según los campos; administra diálogos para editar, eliminar y compartir; prepara la URL para abrirla en el navegador. Para leer, guardar o eliminar búsquedas llama a `SearchesUseCases`. | Sigue en `app` porque es la Activity declarada como entrada en el manifest y necesita coordinar widgets y ciclo de vida Android. Se quitó el acceso directo a preferencias para que la pantalla no conozca el mecanismo de almacenamiento. |
| `TwitterCompositionRoot` — `app` | Extiende `Application`. En `onCreate` instancia `SharedPreferencesSearchesDataSource`, lo conecta con `SharedPreferencesSearchesRepository`, construye `SearchesUseCases` y expone la instancia mediante `SearchesDependencies`. | Reúne en un punto la construcción de objetos concretos. La Activity ya no decide qué repositorio usar; si cambia la persistencia, el cableado se actualiza aquí. |
| `SearchesDependencies` — `application.contracts` | Declara `getSearchesUseCases()`. La implementa `TwitterCompositionRoot` y la consulta `MainActivity` para obtener los casos de uso. | Explicita la dependencia que necesita la UI y evita construir casos de uso o repositorios dentro de la pantalla. |
| `SearchesRepository` — `application.contracts` | Declara las operaciones `getAll()`, `save(TaggedSearch)` y `delete(tag)`. Sus firmas usan la entidad del dominio y no mencionan Android ni `SharedPreferences`. | Es el contrato que necesita la lógica de aplicación. Al depender de una interfaz, `SearchesUseCases` no queda atado a una base de datos o tecnología concreta. |
| `SearchesUseCases` — `application.usecases` | `getSavedSearches()` obtiene la lista; `saveSearch(tag, query)` normaliza espacios, rechaza valores nulos o vacíos y crea un `TaggedSearch`; `deleteSearch(tag)` solicita el borrado. Las tres operaciones pasan por `SearchesRepository`. | Concentra las reglas que antes se podían dispersar en la Activity. La validación se aplica aunque en el futuro otra pantalla utilice estos casos de uso. |
| `TaggedSearch` — `domain.entities` | Modela una búsqueda guardada con `tag` y `query`, constructor y getters. No depende de Android, recursos ni clases de infraestructura. | Es el concepto central que comparten las capas. Mantenerlo como Java simple evita que el modelo dependa de la UI o de cómo se guarda. |
| `SharedPreferencesSearchesDataSource` — `infrastructure.persistence` | Abre el archivo de preferencias `searches`. `getAll()` lee las entradas de texto como un mapa etiqueta/consulta; `save(tag, query)` crea o reemplaza una entrada; `delete(tag)` la elimina. Usa `apply()` para guardar los cambios. | Encapsula la API Android y el detalle del nombre/formato del archivo de preferencias. Ninguna otra capa manipula directamente `SharedPreferences`. |
| `SharedPreferencesSearchesRepository` — `infrastructure.repositories` | Implementa `SearchesRepository`. Convierte el mapa del data source en una lista de `TaggedSearch`; al guardar convierte la entidad en etiqueta y consulta; para eliminar delega la etiqueta al data source. | Traduce entre el contrato usado por la aplicación y el formato clave/valor usado por las preferencias. Así la lógica no necesita saber cómo están representados los datos físicamente. |
| `SearchesAdapter` — `presentation` | Infla `list_item.xml`, asigna una etiqueta a cada fila y conecta los listeners que recibe de la Activity. No consulta el repositorio ni define qué significa guardar o eliminar. | Separa el enlace de datos y filas del código de la pantalla. La Activity sigue decidiendo la acción, mientras el adaptador se encarga de representar la lista. |
| `ItemDivider` — `presentation` | Obtiene el separador del tema Android y lo dibuja debajo de las filas visibles del RecyclerView, excepto la última. | Aísla un detalle gráfico reutilizable para que la Activity no tenga lógica de dibujo. |

`list_item.xml`, los colores de las filas y su dimensión están en `presentation`, junto a las clases que los utilizan. Los layouts principales, tema, iconos y demás recursos de la aplicación permanecen en `app`.

## Cómo se conectan las capas

Al iniciar, `TwitterCompositionRoot` construye esta cadena:

```text
SharedPreferencesSearchesDataSource
    → SharedPreferencesSearchesRepository
        → SearchesUseCases
            → MainActivity
```

Al guardar, `MainActivity` entrega etiqueta y consulta a `saveSearch`. El caso de uso valida y llama al contrato `SearchesRepository`; la implementación de infraestructura convierte los datos y delega la escritura al data source. Al listar, el data source lee el mapa, el repositorio crea entidades `TaggedSearch` y la Activity presenta sus etiquetas. Para abrir o compartir una búsqueda, la Activity obtiene la consulta desde el caso de uso y conserva la acción Android correspondiente.

## Separación de Gradle

| Archivo / módulo | Qué configura | Por qué se separó así |
| --- | --- | --- |
| `settings.gradle.kts` | Nombra el proyecto, define repositorios para plugins y librerías, configura la convención de toolchains e incluye `:app`, `:application`, `:domain`, `:infrastructure` y `:presentation`. | Es el registro de los subproyectos. Hace que Gradle los cargue y resuelva como una compilación multiproyecto. |
| `build.gradle.kts` raíz | Declara los alias de `com.android.application` y `com.android.library` con `apply false`; no aplica plugins Android a la raíz. | Deja disponibles los plugins para todos los módulos y evita declarar versiones distintas en cada script. |
| `gradle/libs.versions.toml` | Centraliza AGP, JUnit, Espresso, AppCompat, Material, ConstraintLayout, RecyclerView y CoordinatorLayout, además de alias de plugins y librerías. | Permite actualizar dependencias desde un solo lugar y reutilizar nombres cortos y consistentes en los scripts. |
| `app/build.gradle.kts` | Usa `com.android.application`; configura `namespace`, `applicationId`, SDK, versión, manifest, compilación Java 11 y dependencias de los otros cuatro módulos. También declara las librerías de UI y pruebas. | `app` genera el APK instalable, declara Activity/Application y combina los recursos y manifest de los módulos Android. |
| `application/build.gradle.kts` | Usa `java-library`, fija compatibilidad Java 11 y declara `api(project(":domain"))`. No configura SDK ni manifest Android. | Los contratos y casos de uso no usan APIs Android. `api` expone `TaggedSearch` a los consumidores porque aparece en las firmas públicas de sus contratos y operaciones. |
| `domain/build.gradle.kts` | Usa `java-library`, configura Java 11 y no depende de otros módulos. | Mantiene las entidades centrales en la capa más independiente; no necesita Android para compilar. |
| `infrastructure/build.gradle.kts` | Usa `com.android.library`, define namespace y SDK mínimo, configura Java 11 y depende de `application` y `domain`. | Necesita Android para acceder a `Context` y `SharedPreferences`; como no es el APK, se publica como biblioteca y puede implementar el contrato de aplicación. |
| `presentation/build.gradle.kts` | Usa `com.android.library`, define namespace y SDK mínimo, configura Java 11 y añade RecyclerView. Incluye el layout y recursos propios de la fila. | El adaptador y el divisor usan Android y RecyclerView, pero no son el punto de entrada ejecutable. La biblioteca agrupa ese código y sus recursos. |

La dependencia de compilación queda dirigida desde `app` hacia los módulos; `application` depende de `domain`, e `infrastructure` implementa los contratos de `application` usando `domain`. Así la lógica central no importa clases Android de infraestructura.



