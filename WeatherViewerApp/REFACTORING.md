# Refactorización del proyecto

## Qué se cambió y por qué

Antes de la refactorización, `MainActivity` reunía la pantalla, las llamadas HTTP, la lectura de respuestas JSON, la conversión entre formatos de servicios y la creación de los elementos del pronóstico. Eso hacía que cambios en el servicio del clima o en el formato de respuesta afectaran también al código de interfaz.

El proyecto se reorganizó en cinco módulos Gradle. `app` conserva el APK, `MainActivity` y `WeatherCompositionRoot`; `presentation` contiene el adaptador y los recursos de las filas; `application` contiene el caso de uso y el contrato del repositorio; `domain` contiene las entidades; e `infrastructure` implementa el acceso HTTP y transforma las respuestas de los proveedores a las entidades del dominio.

La interfaz ahora solicita el pronóstico mediante `GetForecastUseCase`. Este depende de `WeatherRepository`, una interfaz declarada en `application`; `HttpWeatherRepository`, en `infrastructure`, implementa ese contrato. Así, la lógica de aplicación no necesita conocer cómo se obtiene el clima. `WeatherCompositionRoot`, en `app`, es el punto que conecta la implementación HTTP con el caso de uso.

Se conservaron la pantalla, la búsqueda por ciudad, las unidades Fahrenheit, el idioma español y el comportamiento de proveedores: se intenta OpenWeatherMap si hay una clave configurada y, si no está disponible, se utiliza Open-Meteo. La alternativa gratuita resuelve primero la ciudad mediante geocodificación y después consulta el pronóstico diario. La refactorización reorganiza estas responsabilidades sin cambiar el flujo principal de uso.

## Qué hace cada clase

| Clase y ubicación | Qué hace ahora | Por qué quedó así |
| --- | --- | --- |
| `MainActivity` — `app` | Configura la pantalla, recibe la ciudad, oculta el teclado, ejecuta la consulta fuera del hilo de interfaz y actualiza la lista y el subtítulo. Presenta mensajes cuando la ciudad no se encuentra o falla la conexión. | Es la Activity de entrada declarada en el manifest y coordina la interfaz Android. Ya no contiene peticiones HTTP ni convierte respuestas JSON. |
| `WeatherCompositionRoot` — `app` | Crea `HttpWeatherRepository` con la configuración de recursos y lo conecta con `GetForecastUseCase`. | Mantiene en un único punto el cableado de implementaciones concretas, separado del código de la pantalla. |
| `GetForecastUseCase` — `application.usecases` | Valida que la ciudad tenga contenido y delega la consulta al contrato `WeatherRepository`. | Expresa la operación de aplicación sin depender de Android, HTTP ni del formato de un proveedor. |
| `WeatherRepository` — `application.contracts.repositories` | Declara `getForecast(city)` y devuelve un `Forecast`. | Define lo que la aplicación necesita de la fuente del clima sin fijar su tecnología. |
| `Forecast` — `domain.entities` | Agrupa el nombre de la ubicación y la lista de días pronosticados. | Es el resultado de dominio que atraviesa las capas de aplicación y presentación. |
| `Weather` — `domain.entities` | Representa un día con timestamp, temperaturas mínima y máxima, humedad, descripción e identificador del icono. | Mantiene los datos del pronóstico independientes de Android y de las respuestas JSON externas. |
| `HttpWeatherRepository` — `infrastructure.repositories` | Consulta OpenWeatherMap cuando hay una clave configurada; si no, usa geocodificación y pronóstico diario de Open-Meteo. Lee JSON, transforma ambas respuestas a `Forecast` y `Weather`, y cierra las conexiones HTTP. | Encapsula los detalles de red y de los proveedores detrás del contrato de aplicación. |
| `WeatherArrayAdapter` — `presentation` | Representa cada día en el `ListView`, formatea fecha, temperaturas y humedad, y carga y mantiene en caché los iconos. | Separa la representación de cada fila de la Activity y agrupa el adaptador con sus recursos específicos. |

El layout principal, la Activity y los recursos generales de la aplicación permanecen en `app`. `list_item.xml` y las cadenas que utiliza el adaptador se encuentran en `presentation`.

## Cómo se conectan las capas

Al iniciar la pantalla, `WeatherCompositionRoot` construye la cadena de dependencias:

```text
MainActivity
    → WeatherCompositionRoot
        → GetForecastUseCase
            → WeatherRepository (contrato)
                ← HttpWeatherRepository (implementación)
                    → Forecast y Weather
                        → WeatherArrayAdapter
```

Cuando el usuario busca una ciudad, `MainActivity` ejecuta `GetForecastUseCase` en una tarea de fondo. El caso de uso valida la entrada y llama al contrato; `HttpWeatherRepository` obtiene los datos del proveedor disponible y los convierte a entidades de dominio. La Activity entrega los días al adaptador para mostrarlos.

## Separación de Gradle

| Archivo o módulo | Qué configura | Por qué se separó así |
| --- | --- | --- |
| `settings.gradle.kts` | Registra `:app`, `:application`, `:domain`, `:infrastructure` y `:presentation`. | Hace que Gradle cargue cada responsabilidad como subproyecto independiente. |
| `build.gradle.kts` raíz | Declara los plugins Android de aplicación y biblioteca con `apply false`. | Centraliza la disponibilidad de los plugins sin aplicarlos al proyecto raíz. |
| `gradle/libs.versions.toml` | Centraliza las versiones de AGP y las dependencias Android y de pruebas usadas por los módulos. | Permite administrar versiones y alias desde un solo catálogo. |
| `app/build.gradle.kts` | Aplica el plugin de aplicación; define namespace, `applicationId`, SDK, versión, Java 11 y dependencias de módulos y librerías de interfaz. | Produce el APK y combina el manifest y los recursos de la aplicación y sus bibliotecas. |
| `application/build.gradle.kts` | Usa `java-library`, fija compatibilidad Java 11 y depende de `domain`. | Los contratos y casos de uso no requieren un módulo Android. |
| `domain/build.gradle.kts` | Usa `java-library` y configura Java 11, sin dependencias de otros módulos. | Mantiene las entidades centrales en el módulo más independiente. |
| `infrastructure/build.gradle.kts` | Usa `com.android.library`, configura namespace, SDK mínimo y Java 11; depende de `application` y `domain`. | Usa Android para JSON y red, pero no genera el APK ejecutable. Implementa el contrato de aplicación. |
| `presentation/build.gradle.kts` | Usa `com.android.library`, configura namespace, SDK mínimo y Java 11; depende de `domain` y AppCompat. | Agrupa el adaptador Android y los recursos que necesita sin convertirlos en parte de la capa de aplicación. |

Las dependencias de compilación se dirigen desde `app` hacia los módulos. `application` depende de `domain`; `infrastructure` depende de los contratos de `application` y de las entidades; `presentation` depende del modelo de `domain`. Las capas internas no importan implementaciones de infraestructura.

