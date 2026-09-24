# Refactorización del proyecto

## Qué se cambió y por qué

Antes de la refactorización, las clases del juego, los casos de uso, el dibujo de Android y el acceso al audio vivían dentro del mismo módulo `app`. Las entidades del dominio utilizaban clases gráficas de Android como `Canvas`, `Paint` y `Rect`, y el caso de uso que iniciaba una partida también dependía de `Color`. Además, `CannonView` construía directamente `SoundPoolRepository`. Esto mezclaba reglas del juego con detalles de la plataforma y hacía que las capas dependieran más entre sí.

El proyecto se reorganizó en cinco módulos Gradle. `domain` contiene las entidades y las reglas básicas del juego; `application` agrupa los casos de uso y el contrato de sonido; `infrastructure` implementa ese contrato con Android `SoundPool`; `presentation` contiene la Activity Fragment, la vista, el ciclo de juego y el renderizado; y `app` genera la aplicación instalable y conecta las implementaciones concretas.

También se separó la representación visual del modelo. Las entidades ahora conservan dimensiones y posiciones numéricas y no dibujan en un `Canvas`. `CannonRenderer` se encarga del dibujo, los colores y los textos. `StartNewGameUseCase` crea las entidades sin recibir colores de Android. Así, los cambios visuales se pueden realizar en presentación sin modificar las reglas del juego.

El audio se invoca a través de `SoundRepository`, una interfaz declarada en `application`. `SoundPoolRepository`, que conoce `Context`, `SoundPool` y los recursos de audio, implementa el contrato en `infrastructure`. `GameCompositionRoot`, en `app`, crea esa implementación, construye los casos de uso y entrega el conjunto a la interfaz mediante `GameRuntime`.

La separación busca reducir el acoplamiento y aclarar dónde modificar cada comportamiento: reglas y entidades en `domain`; coordinación del juego en `application`; servicios de Android en `infrastructure`; interfaz y renderizado en `presentation`; y configuración del arranque y dependencias en `app`.

Se conservó `applicationId` (`fisei.uta.edu.ec.cannongameapp`), la Activity de entrada, los recursos de la aplicación y el flujo visible del juego. La refactorización reorganizó el código y sus dependencias sin cambiar la finalidad de la aplicación.

## Qué hace cada clase

| Clase y ubicación | Qué hace ahora | Por qué quedó así |
| --- | --- | --- |
| `MainActivity` — `app` | Configura la orientación horizontal, carga el layout principal, localiza `MainActivityFragment` y le entrega las dependencias creadas por `GameCompositionRoot`. | Es el punto de entrada Android y coordina el arranque de la interfaz. |
| `GameCompositionRoot` — `app` | Crea `SoundPoolRepository`, los casos de uso y un `GameRuntime` que se pasa a la interfaz. | Es el punto de composición: aquí se eligen y conectan las implementaciones concretas sin construirlas desde `CannonView`. |
| `GameRuntime` — `presentation` | Agrupa el contrato de sonido y los casos de uso que necesita `CannonView`. | Permite entregar las dependencias a presentación desde el módulo ejecutable sin hacer que `presentation` dependa de `app`. |
| `MainActivityFragment` — `presentation` | Infla `fragment_main`, configura el control de volumen y entrega el `GameRuntime` a `CannonView`. En el ciclo de vida detiene el juego y libera el audio. | Mantiene en un Fragment la conexión entre el layout y la vista del juego, además de sus responsabilidades de ciclo de vida. |
| `CannonView` — `presentation` | Recibe entradas táctiles, administra las dimensiones y los callbacks de la superficie, coordina el bucle del juego, invoca los casos de uso y presenta el diálogo al terminar la partida. | Es la frontera entre los eventos y el ciclo de vida de Android y la lógica expuesta por `application`. Ya no instancia el repositorio concreto de sonido. |
| `CannonRenderer` — `presentation` | Dibuja fondo, texto, cañón, proyectil, obstáculo y objetivos en el `Canvas`; aplica colores y estilos. | Los detalles gráficos pertenecen a presentación y no a las entidades del dominio. |
| `GameLoopThread` — `presentation` | Ejecuta el bucle que calcula el tiempo entre cuadros y notifica a `CannonView` para actualizar y renderizar. | Aísla el trabajo del hilo de juego de la vista y del modelo. |
| `GameOverDialogHelper` — `presentation` | Muestra el diálogo de victoria o derrota y notifica cuando el usuario solicita reiniciar. | Encapsula un detalle de interfaz Android que no forma parte de las reglas del juego. |
| `SoundRepository` — `application.contracts.repositories` | Declara las operaciones para reproducir los sonidos del disparo, el impacto en objetivos y el impacto en el obstáculo, además de liberar recursos. | Es el contrato que necesitan los casos de uso; sus firmas no exponen `SoundPool`. |
| `StartNewGameUseCase` — `application.usecases` | Crea el cañón, el obstáculo, los objetivos y el estado inicial para las dimensiones de pantalla recibidas. | Agrupa la configuración de una partida en una operación de aplicación y no depende de colores ni clases gráficas Android. |
| `FireCannonballUseCase` — `application.usecases` | Calcula el ángulo del cañón a partir del toque, dispara cuando corresponde, cuenta el disparo y solicita el sonido mediante `SoundRepository`. | Coordina la acción de disparar y sus efectos usando el contrato de aplicación. |
| `UpdateGamePositionsUseCase` — `application.usecases` | Actualiza las posiciones del proyectil, el obstáculo y los objetivos, y avanza el tiempo mientras la partida está activa. | Centraliza la actualización de las entidades del juego. |
| `CheckCollisionsUseCase` — `application.usecases` | Comprueba impactos con objetivos y obstáculo, aplica recompensas o penalizaciones, elimina objetivos alcanzados y determina la victoria. Solicita los sonidos mediante el contrato. | Mantiene la coordinación de las reglas de colisión en aplicación, apoyándose en el dominio y en una interfaz para audio. |
| `GetGameStateUseCase` — `application.usecases` | Expone consultas sobre el fin y resultado de la partida, el tiempo restante, los disparos y el tiempo transcurrido. | Reúne las consultas que necesita la interfaz sobre el estado de juego. |
| `GameElement` — `domain.entities` | Representa un elemento rectangular móvil con posición, dimensiones y velocidad vertical; actualiza su movimiento y rebote. | Mantiene datos y comportamiento del juego sin usar `Rect`, `Paint` ni `Canvas` de Android. |
| `Cannon` — `domain.entities` | Conserva dimensiones y ángulo del cañón, calcula el disparo y administra el proyectil actual. | Contiene el comportamiento del cañón sin encargarse de dibujarlo. |
| `Cannonball` — `domain.entities` | Representa el proyectil, actualiza su movimiento horizontal y vertical, detecta colisiones y determina si sigue dentro de la pantalla. | Mantiene las reglas geométricas del proyectil independientes del renderizado Android. |
| `Target` y `Blocker` — `domain.entities` | Modelan los objetivos y el obstáculo, incluyendo la recompensa por impacto o la penalización por fallo. | Representan conceptos del juego y sus datos asociados, no widgets ni recursos visuales. |
| `GameState` — `domain.entities` | Mantiene el tiempo, los disparos, el tiempo transcurrido y los indicadores de victoria o fin. | Concentra el estado y las transiciones principales de la partida. |
| `GameConfig` — `domain.entities` | Agrupa constantes de reglas y valores proporcionales del juego, como dimensiones y velocidad; el tamaño del texto también se consulta desde el renderizador. | Evita dispersar valores de configuración por varias clases. |
| `SoundPoolRepository` — `infrastructure.repositories` | Implementa `SoundRepository` con `SoundPool`, carga los archivos de audio desde `infrastructure/src/main/res/raw` y libera el reproductor. | Encapsula los detalles de audio y recursos Android en infraestructura. |

Los layouts y recursos de color y texto de la interfaz están en `presentation/src/main/res`. Los sonidos están en `infrastructure/src/main/res/raw`. El manifiesto, tema, iconos, reglas de respaldo y nombre de la aplicación permanecen en `app/src/main`.

## Cómo se conectan las capas

Al iniciar, la Activity pide al punto de composición las dependencias; este construye la implementación concreta de sonido y los casos de uso. La interfaz recibe el grupo mediante `GameRuntime`:

```text
MainActivity (app)
    → GameCompositionRoot (app)
        → SoundPoolRepository (infrastructure)
        → casos de uso (application)
            → GameRuntime (presentation)
                → MainActivityFragment / CannonView (presentation)
```

Durante el juego, `CannonView` llama a los casos de uso para crear la partida, disparar, actualizar posiciones y comprobar colisiones. Los casos de uso operan sobre entidades del dominio. Cuando necesitan sonido, usan el contrato `SoundRepository`; `SoundPoolRepository` implementa ese contrato:

```text
CannonView → caso de uso → entidades de domain
                         → SoundRepository ← SoundPoolRepository
```

Al renderizar, `CannonView` entrega el estado actual a `CannonRenderer`, que convierte las posiciones y dimensiones del dominio en primitivas de dibujo Android. El dominio no importa clases de presentación o infraestructura.

## Separación de Gradle

| Archivo / módulo | Qué configura | Por qué se separó así |
| --- | --- | --- |
| `settings.gradle.kts` | Declara repositorios y toolchains e incluye `:app`, `:application`, `:domain`, `:infrastructure` y `:presentation`. | Registra los cinco subproyectos que Gradle debe configurar y compilar. |
| `build.gradle.kts` raíz | Declara los plugins Android de aplicación y biblioteca con `apply false`. | Centraliza los plugins disponibles sin aplicarlos al proyecto raíz. |
| `gradle/libs.versions.toml` | Centraliza versiones y alias de AGP, JUnit, AndroidX Test, Espresso, AppCompat, Material, Activity KTX y ConstraintLayout. | Permite mantener las versiones y nombres de dependencias desde un solo catálogo. |
| `app/build.gradle.kts` | Usa el plugin `com.android.application`, define namespace, `applicationId`, SDK, versión, Java 11 y dependencias hacia los otros cuatro módulos. | Genera el APK instalable y es el módulo que conecta e inicia la aplicación. |
| `domain/build.gradle.kts` | Usa `java-library`, configura compatibilidad Java 11 y no depende de otros módulos. | Mantiene el núcleo del juego compilable sin Android ni capas externas. |
| `application/build.gradle.kts` | Usa `java-library`, configura Java 11 y declara `api(project(":domain"))`. | Los casos de uso y contratos usan entidades del dominio y no necesitan Android. |
| `infrastructure/build.gradle.kts` | Usa `com.android.library`, configura namespace, `minSdk` 24, Java 11 y dependencia con `application`. | Necesita APIs Android para implementar el audio, pero no produce el APK ejecutable. |
| `presentation/build.gradle.kts` | Usa `com.android.library`, configura namespace, `minSdk` 24, Java 11 y dependencias `api` con `application` y `domain`, además de librerías de interfaz. | Agrupa la UI y sus recursos en una biblioteca Android; sus clases usan tipos de aplicación y dominio en sus interfaces. |

La dirección de dependencias queda así: `application` depende de `domain`; `presentation` depende de `application` y `domain`; `infrastructure` depende de `application`; y `app` depende de los cuatro módulos para ensamblar la aplicación. Las capas interiores no dependen de `app` ni de la implementación concreta de audio.
