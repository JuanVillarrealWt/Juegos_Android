# Documentación Técnica: Reestructuración a Clean Architecture
**Proyecto:** Doodlz5  
**Paquete Principal:** `fisei.uta.ec.doodlz5`  
**Lenguaje:** Java 11 (Android SDK 36)  

---

## 1. Introducción y Propósito

El objetivo principal de este trabajo es la transformación de una estructura de software monolítica y plana a una arquitectura limpia basada en **Clean Architecture** (Robert C. Martin). 

En la versión original del proyecto, la totalidad de las clases residían en un único paquete plano (`fisei.uta.ec.doodlz5`). Esta organización generaba un alto acoplamiento entre la interfaz de usuario (Android Framework), los sensores de hardware (acelerómetro), la gestión de dibujos en lienzo (`Canvas`) y los servicios de infraestructura (almacenamiento en `MediaStore` e impresión mediante `PrintHelper`).

Con la nueva arquitectura se logra:
- **Separación de responsabilidades (SoC):** Cada clase tiene un único motivo para cambiar.
- **Inversión de dependencias (DIP):** Los componentes de alto nivel (casos de uso) no dependen de detalles de bajo nivel (Android SDK), sino de abstracciones (interfaces).
- **Mantenibilidad y Escalabilidad:** Es posible cambiar la tecnología de almacenamiento o la UI sin afectar las reglas del negocio.

---

## 2. Diagrama de Arquitectura y Flujo de Dependencias

El siguiente diagrama ilustra el flujo de dependencias entre las capas del proyecto. La **Regla de Dependencia** establece que las dependencias deben apuntar únicamente hacia adentro, hacia el Dominio y la Aplicación.

```
+-----------------------------------------------------------------------+
|                         PRESENTATION LAYER                            |
|   (MainActivity, MainActivityFragment, Dialogs, DoodleView)           |
+-----------------------------------++----------------------------------+
                                    || Usa
                                    \/
+-----------------------------------------------------------------------+
|                         COMPOSITION LAYER                             |
|                     (DoodleCompositionRoot)                           |
+-----------------------------------++----------------------------------+
                                    || Inyecta
                                    \/
+-----------------------------------------------------------------------+
|                         APPLICATION LAYER                             |
|  - Contracts: ImageRepository, PrintRepository, ShakeSensorRepository |
|  - Use Cases: SaveDoodleUseCase, PrintDoodleUseCase, ClearDoodle...   |
+-----------------------------------++----------------------------------+
                                    || Usa
                                    \/
+-----------------------------------------------------------------------+
|                           DOMAIN LAYER                                |
|                   (DoodleColor, DoodleLineWidth)                      |
+-----------------------------------------------------------------------+
                                    /\
                                    || Implementa interfaces de contrato
+-----------------------------------++----------------------------------+
|                       INFRASTRUCTURE LAYER                            |
|   (MediaStoreImageRepositoryImpl, AndroidPrintRepositoryImpl,          |
|    AccelerometerSensorRepositoryImpl)                                 |
+-----------------------------------------------------------------------+
```

---

## 3. Desglose Detallado por Capa

### 3.1. Capa de Dominio (`fisei.uta.ec.doodlz5.domain.entities`)
Representa el núcleo del sistema. Contiene los objetos y conceptos puros del negocio sin depender de ninguna librería de Android.

* **`DoodleColor.java`**: Modela el color de un trazo de dibujo mediante componentes ARGB (Alpha, Red, Green, Blue). Aísla la representación del color del framework gráfico de Android.
* **`DoodleLineWidth.java`**: Modela el grosor del pincel utilizado para dibujar.

---

### 3.2. Capa de Aplicación (`fisei.uta.ec.doodlz5.application`)
Contiene las reglas de aplicación del sistema y orquesta los flujos de datos hacia y desde las entidades.

#### 3.2.1. Contratos / Interfaces (`application.contracts.repositories`)
Definen las operaciones de persistencia e infraestructura que la aplicación necesita, sin conocer cómo están implementadas en el sistema operativo:
* **`ImageRepository.java`**: Interfaz para guardar la imagen del lienzo.
* **`PrintRepository.java`**: Interfaz para enviar la imagen al servicio de impresión.
* **`ShakeSensorRepository.java`**: Interfaz para abstraer la escucha de eventos de sacudida (*shake*).

#### 3.2.2. Casos de Uso (`application.usecases`)
Cada caso de uso representa una acción concreta que el usuario o el sistema puede realizar (Principio de Responsabilidad Única):
* **`SaveDoodleUseCase.java`**: Recibe la imagen y la información requerida y delega la persistencia al `ImageRepository`.
* **`PrintDoodleUseCase.java`**: Delega la impresión del lienzo al `PrintRepository`.
* **`ClearDoodleUseCase.java`**: Ejecuta la acción de reiniciar o borrar el lienzo.
* **`ChangeColorUseCase.java`**: Aplica un nuevo `DoodleColor` al lienzo.
* **`ChangeLineWidthUseCase.java`**: Aplica un nuevo `DoodleLineWidth` al lienzo.
* **`EvaluateShakeUseCase.java`**: Contiene la fórmula matemática para calcular si la aceleración detectada supera el umbral de sacudida ($100,000$).

---

### 3.3. Capa de Infraestructura (`fisei.uta.ec.doodlz5.infrastructure`)
Implementa las interfaces definidas en la capa de aplicación interactuando directamente con las APIs de Android SDK.

#### 3.3.1. Persistencia (`infrastructure.persistence`)
* **`MediaStoreImageRepositoryImpl.java`**: Implementa `ImageRepository`. Utiliza `MediaStore.Images.Media.insertImage` para guardar la imagen en el almacenamiento externo/galería del dispositivo.
* **`AndroidPrintRepositoryImpl.java`**: Implementa `PrintRepository`. Utiliza `androidx.print.PrintHelper` para interactuar con el subsistema de impresión de Android.

#### 3.3.2. Sensores (`infrastructure.repositories`)
* **`AccelerometerSensorRepositoryImpl.java`**: Implementa `ShakeSensorRepository`. Se registra ante el `SensorManager` de Android para escuchar eventos del acelerómetro (`Sensor.TYPE_ACCELEROMETER`) y utiliza `EvaluateShakeUseCase` para determinar cuando ocurrió una sacudida válida, notificando a la UI mediante un *callback*.

---

### 3.4. Raíz de Composición (`fisei.uta.ec.doodlz5.composition`)
* **`DoodleCompositionRoot.java`**: Implementa el patrón **Singleton** y actúa como un contenedor de Inyección de Dependencias manual (*Service Locator*). Crea las instancias únicas de los repositorios y de los casos de uso, desacoplando completamente a la vista de la construcción directa de objetos de infraestructura.

---

### 3.5. Capa de Presentación (`fisei.uta.ec.doodlz5.presentation`)
Contiene los componentes con los que interactúa el usuario. Se encuentra organizada en subpaquetes según el tipo de componente visual:

#### 3.5.1. Actividades (`presentation.ui.activities`)
* **`MainActivity.java`**: Actividad principal. Configura la barra de herramientas (`Toolbar`), maneja los márgenes del sistema (*Edge-to-Edge insets*) y fuerza la orientación según el tamaño de la pantalla.

#### 3.5.2. Fragmentos y Diálogos (`presentation.ui.fragments`)
* **`MainActivityFragment.java`**: Fragmento principal. Aloja el `DoodleView`, infla el menú de opciones (`color`, `line_width`, `delete`, `save`, `print`) y delega las operaciones a los UseCases a través de `DoodleCompositionRoot`. Además, maneja la solicitud de permisos de almacenamiento en runtime.
* **`ColorDialogFragment.java`**: Diálogo con barras deslizantes (`SeekBar`) para seleccionar un color ARGB. Al presionar aceptar, ejecuta `ChangeColorUseCase`.
* **`LineWidthDialogFragment.java`**: Diálogo con barra deslizante para seleccionar el grosor del pincel con una vista previa visual. Al aceptar, ejecuta `ChangeLineWidthUseCase`.
* **`EraseImageDialogFragment.java`**: Diálogo de confirmación para borrar el dibujo. Al confirmar, ejecuta `ClearDoodleUseCase`.

#### 3.5.3. Vistas Personalizadas (`presentation.ui.views`)
* **`DoodleView.java`**: Vista personalizada que hereda de `android.view.View`. Se encarga únicamente del dibujo interactivo (eventos de toque `onTouchEvent`, renderizado en `onDraw` sobre un `Bitmap`/`Canvas`). Implementa las interfaces requeridas por los casos de uso (`ClearableLienzo`, `ColorableLienzo`, `WidthChangeableLienzo`).

---

## 4. Cambios en Archivos de Configuración y Layouts XML

Debido al cambio de paquetes de las clases Java de la UI, se actualizaron las referencias cualificadas en los siguientes archivos de recursos sin alterar ningún diseño ni estilo:

1. **`AndroidManifest.xml`**:
   Se cambió la declaración de la actividad principal a su nuevo paquete:
   ```xml
   <activity android:name=".presentation.ui.activities.MainActivity" ... />
   ```

2. **`content_main.xml`**:
   Se actualizó el nombre del fragmento contenido:
   ```xml
   android:name="fisei.uta.ec.doodlz5.presentation.ui.fragments.MainActivityFragment"
   ```

3. **`fragment_main.xml`**:
   Se actualizó la etiqueta de la vista personalizada:
   ```xml
   <fisei.uta.ec.doodlz5.presentation.ui.views.DoodleView ... />
   ```

---

## 5. Tabla Comparativa: Antes vs. Después

| Aspecto | Estructura Anterior | Nueva Estructura Clean Architecture |
| :--- | :--- | :--- |
| **Organización de Paquetes** | 1 único paquete plano (`fisei.uta.ec.doodlz5`) | 5 capas bien delimitadas (`domain`, `application`, `infrastructure`, `presentation`, `composition`) |
| **Guardado de Imagen** | Directamente dentro de `DoodleView.java` invocando `MediaStore` | Desacoplado mediante `SaveDoodleUseCase` e `ImageRepository` / `MediaStoreImageRepositoryImpl` |
| **Impresión** | Directamente dentro de `DoodleView.java` invocando `PrintHelper` | Desacoplado mediante `PrintDoodleUseCase` y `PrintRepository` / `AndroidPrintRepositoryImpl` |
| **Sensor Acelerómetro** | Implementado directamente en `MainActivityFragment.java` | Aislado en `AccelerometerSensorRepositoryImpl` y evaluado por `EvaluateShakeUseCase` |
| **Construcción de Objetos** | Acoplamiento directo entre diálogos, fragmentos y vistas | Inyección centralizada a través de `DoodleCompositionRoot` |
| **Testabilidad Unitaria** | Difícil debido al alto acoplamiento con clases Android (`View`, `Fragment`) | Muy alta. Los UseCases y entidades se pueden probar con JUnit puro sin emulador |

---

## 6. Conclusión y Evaluación

La reestructuración realizada transforma el proyecto **Doodlz5** en una aplicación mantenible, modular y alineada con los estándares de la industria del desarrollo en Android. Esta arquitectura facilita la adición de nuevas características (por ejemplo, guardar en base de datos Room, cambiar a almacenamiento en la nube o migrar la UI a Jetpack Compose) sin modificar la lógica fundamental del dominio.
