# TipCalculator - Arquitectura y Documentación Técnica

Aplicación Android nativa desarrollada en Java para el cálculo de propinas y totales sobre cuentas de consumo. El proyecto ha sido refactorizado aplicando **Clean Architecture** (Arquitectura Limpia en capas) y los principios **SOLID**, desacoplando la lógica de negocio, la presentación visual y las utilidades de formateo e interacción de entrada.

---

## 1. Qué se cambió y por qué

### Situación inicial (Bloque único)
Originalmente, la totalidad del comportamiento de la aplicación se encontraba agrupado en un único archivo (`MainActivity.java`). En este bloque monolítico convivían:
- Inflado y control del ciclo de vida de la actividad Android.
- Manipulación y posicionamiento del cursor en el `EditText` anteponiendo el símbolo `$`.
- Parseo y validación de cadenas de texto a tipos numéricos `double`.
- Lógica matemática de negocio (cálculo de propina y cálculo del total).
- Formateo de valores numéricos a representaciones de moneda (`Locale.US`) y porcentaje.
- Actualización directa de los elementos gráficos en el árbol de vistas.

Esta concentración de responsabilidades generaba:
- **Alto acoplamiento y baja cohesión:** Cualquier ajuste (por ejemplo, cambiar la regla de propina o el formato de moneda) requería modificar la actividad gráfica.
- **Dificultad de pruebas unitarias:** Era imposible probar la lógica de cálculo sin levantar el entorno de Android o un emulador.
- **Violación de principios de diseño:** Rompía el principio de responsabilidad única (God Activity) y cerraba el código a extensiones modulares.

### Situación actual (Clean Architecture + SOLID)
Se segregó el código en capas independientes con responsabilidades delimitadas y contratos claros:
- **Dominio (`domain`):** Lógica pura de cálculo sin dependencias del SDK de Android.
- **Presentación (`presentation`):** Adopción del patrón MVVM mediante `ViewModel` y `LiveData`, convirtiendo la `MainActivity` en una vista pasiva.
- **Utilidades (`util`):** Formateadores aislados de moneda y porcentaje, junto con un `TextWatcher` dedicado al comportamiento del campo de texto.

### Tabla comparativa: Estado inicial vs. Estado actual

| Aspecto | Implementación Inicial (Monolito) | Implementación Refactorizada (Clean Architecture + SOLID) | Beneficio Obtenido |
| :--- | :--- | :--- | :--- |
| **Organización de archivos** | Todo dentro de `MainActivity.java`. | Estructura modular dividida en `domain`, `presentation` y `util`. | Alta mantenibilidad, legibilidad y navegación clara del proyecto. |
| **Lógica de negocio** | Cálculo manual incrustado dentro de métodos de la UI. | Encapsulada en `CalculateTipUseCase` y `CalculateTipUseCaseImpl`. | Independencia total de frameworks; testeable con pruebas unitarias puras en JVM (JUnit). |
| **Manejo del estado** | Variables primitivas mutables globales en la Activity (`billAmount`, `percent`). | `MainViewModel` centraliza el estado emitiendo `TipUiState` inmutable vía `LiveData`. | Persistencia frente a rotaciones de pantalla y ciclo de vida; estado predecible y unidireccional. |
| **Formateo de texto** | Instancias estáticas de `NumberFormat` invocadas directamente en la Activity. | Interfaces `ICurrencyFormatter` e `IPercentFormatter` con implementaciones dedicadas. | Reutilización, facilidad de reemplazo (LSP) y cumplimiento de Inversión de Dependencias (DIP). |
| **Control del `EditText`** | `TextWatcher` anónimo con lógica de cursor y cálculos mezclada. | Clase independiente `AmountInputWatcher` con callback `OnAmountChangedListener`. | Desacoplamiento del parseo de entrada respecto a la vista y al modelo de cálculo. |
| **Acoplamiento de dependencias** | Dependencia rígida de implementaciones concretas. | Inyección manual de dependencias basada en interfaces (DIP). | Facilidad para realizar mocks o sustituciones en pruebas y escalabilidad futura. |

---

## 2. Qué hace cada clase

A continuación se detalla la función de cada componente organizado por sus respectivas capas:

### Capa de Dominio (`domain`)
Contiene las reglas de negocio esenciales. No posee ninguna referencia al framework de Android (`android.*`).

- **`model/TipResult.java`**: Modelo de datos inmutable que almacena el resultado del cálculo (`tip` y `total`).
- **`usecase/CalculateTipUseCase.java`**: Contrato (interfaz) que define la operación de cálculo (`calculate(double billAmount, double percent)`). Cumple con el principio de Segregación de Interfaces (ISP) e Inversión de Dependencias (DIP).
- **`usecase/CalculateTipUseCaseImpl.java`**: Implementación concreta de las reglas matemáticas para obtener propina y total. Aplica el principio Abierto/Cerrado (OCP) al exponer métodos protegidos (`computeTip` y `computeTotal`) que permiten extender la lógica (descuentos, propinas mínimas, redondeos) mediante herencia o composición sin alterar el código existente.

### Capa de Utilidades / Soporte (`util`)
Encapsula operaciones auxiliares reutilizables de formateo y manejo de eventos de entrada:

- **`ICurrencyFormatter.java`**: Interfaz que define el contrato de formateo monetario.
- **`CurrencyFormatter.java`**: Implementación basada en `NumberFormat.getCurrencyInstance(Locale.US)`. Responsabilidad única de dar formato de divisa a valores numéricos.
- **`IPercentFormatter.java`**: Interfaz que define el contrato para formatear valores a representación porcentual.
- **`PercentFormatter.java`**: Implementación basada en `NumberFormat.getPercentInstance()`. Responsabilidad única de transformar fracciones decimales a porcentaje.
- **`AmountInputWatcher.java`**: `TextWatcher` desacoplado que gestiona la entrada de texto del monto:
  - Elimina el símbolo `$` para extraer el valor numérico limpio.
  - Formatea la caja de texto anteponiendo `$` de manera controlada sin generar bucles infinitos de edición.
  - Calcula y reubica adecuadamente el cursor (caret).
  - Emite el valor decimal parseado mediante la interfaz `OnAmountChangedListener`.

### Capa de Presentación (`presentation`)
Implementa la interfaz de usuario siguiendo el patrón MVVM y los componentes de arquitectura de Android:

- **`TipUiState.java`**: Objeto inmutable que modela el estado completo de la pantalla. Contiene tanto los valores numéricos crudos (`billAmount`, `percent`, `tip`, `total`) como las cadenas ya formateadas (`percentFormatted`, `tipFormatted`, `totalFormatted`, `amountFormatted`).
- **`MainViewModel.java`**: Mantiene el estado de la UI y gestiona la comunicación con el caso de uso y los formateadores. Expone un `LiveData<TipUiState>` que la vista observa. Depende exclusivamente de abstracciones inyectadas por constructor.
- **`MainViewModelFactory.java`**: Factoría requerida por Android Jetpack para instanciar `MainViewModel` inyectándole manualmente las dependencias requeridas (`CalculateTipUseCase`, `ICurrencyFormatter`, `IPercentFormatter`).
- **`MainActivity.java`**: Vista pasiva (`View`). Sus responsabilidades se limitan a:
  - Inflar el diseño `activity_main.xml`.
  - Instanciar dependencias e inicializar el ViewModel mediante la factoría.
  - Asociar `AmountInputWatcher` al `EditText` y el listener al `SeekBar`.
  - Observar el `LiveData` del ViewModel y volcar el texto formateado directamente en los `TextView`. **No realiza cálculos matemáticos ni formateos.**

### Resumen de componentes del proyecto

| Capa | Archivo / Componente | Tipo | Responsabilidad Principal |
| :--- | :--- | :--- | :--- |
| **Domain** | `TipResult` | Clase (Model) | Encapsular de forma inmutable los valores de propina y total calculados. |
| **Domain** | `CalculateTipUseCase` | Interfaz (Contract) | Definir la abstracción para el cálculo del caso de uso. |
| **Domain** | `CalculateTipUseCaseImpl` | Clase (UseCase) | Ejecutar las operaciones matemáticas de propina y total (extensible vía OCP). |
| **Util** | `ICurrencyFormatter` | Interfaz | Contrato para la conversión de números a formato monetario. |
| **Util** | `CurrencyFormatter` | Clase | Formatear montos con divisa estadounidense (`$#,##0.00`). |
| **Util** | `IPercentFormatter` | Interfaz | Contrato para la conversión de valores decimales a porcentaje. |
| **Util** | `PercentFormatter` | Clase | Formatear valores a texto porcentual (`#%`). |
| **Util** | `AmountInputWatcher` | Clase (`TextWatcher`) | Gestionar validación, prefijo `$` y posición del cursor en el `EditText`. |
| **Presentation** | `TipUiState` | Clase (UI State) | Representar el estado completo e inmutable que requiere la interfaz. |
| **Presentation** | `MainViewModel` | Clase (`ViewModel`) | Coordinar flujo de datos, retener estado y notificar cambios vía `LiveData`. |
| **Presentation** | `MainViewModelFactory` | Clase (`Factory`) | Proveer inyección de dependencias manual hacia el ViewModel. |
| **Presentation** | `MainActivity` | Clase (`Activity`) | Vista pasiva que enlaza eventos de usuario y renderiza el estado observado. |

---

## 3. Cómo se conectan las capas

### Regla de dependencias
La arquitectura sigue la regla fundamental de Clean Architecture: **las dependencias apuntan hacia adentro**:
```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                     │
│    (MainActivity ──► MainViewModel ──► TipUiState)          │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
               ▼                               ▼
┌─────────────────────────────┐ ┌─────────────────────────────┐
│       CAPA DE DOMINIO       │ │     CAPA DE UTILIDADES      │
│ (CalculateTipUseCase/Impl)  │ │ (Formatters / InputWatcher) │
│       (TipResult)           │ │                             │
└─────────────────────────────┘ └─────────────────────────────┘
```
- La **Capa de Dominio** es completamente independiente y autónoma; no conoce a la Vista ni al ViewModel.
- La **Capa de Presentación** consume el Dominio a través de abstracciones (`CalculateTipUseCase`) y las utilidades mediante interfaces (`ICurrencyFormatter`, `IPercentFormatter`).
- No existen dependencias circulares.

### Flujo de ejecución paso a paso

```
[Usuario interactúa con la UI]
         │
         ├──► Escribe en EditText ──► AmountInputWatcher parsea valor limpio
         │                                       │
         └──► Desplaza SeekBar   ──► onProgressChanged calcula porcentaje
                                                 │
                                                 ▼
                                     MainViewModel recibe evento
                                     (setBillAmount / setPercent)
                                                 │
                                                 ▼
                                     CalculateTipUseCase.calculate(...)
                                                 │
                                                 ▼
                                     Retorna modelo TipResult (tip, total)
                                                 │
                                                 ▼
                                     MainViewModel formatea valores con
                                     ICurrencyFormatter e IPercentFormatter
                                                 │
                                                 ▼
                                     Emite nuevo TipUiState en MutableLiveData
                                                 │
                                                 ▼
                                     MainActivity observa LiveData.onChange()
                                                 │
                                                 ▼
                                     TextViews actualizan sus textos (vista pasiva)
```

1. **Interacción del usuario:**
   - Si el usuario ingresa un monto en `editTextAmount`, `AmountInputWatcher` limpia el texto, formatea el campo con `$` ajustando el cursor, y notifica el valor numérico parseado llamando a `viewModel.setBillAmount(amount)`.
   - Si el usuario mueve `seekBarPercent`, el listener notifica el nuevo porcentaje llamando a `viewModel.setPercent(progress / 100.0)`.
2. **Procesamiento en ViewModel:**
   - `MainViewModel` actualiza su estado interno y delega el cálculo matemático al caso de uso inyectado (`CalculateTipUseCase.calculate(billAmount, percent)`).
3. **Ejecución en Dominio:**
   - `CalculateTipUseCaseImpl` computa la propina (`billAmount * percent`) y el total (`billAmount + tip`), empaquetándolos en un objeto inmutable `TipResult`.
4. **Transformación a Estado de UI:**
   - `MainViewModel` toma el `TipResult` y genera las cadenas listas para mostrar invocando `currencyFormatter.format(...)` y `percentFormatter.format(...)`.
   - Construye una nueva instancia inmutable de `TipUiState`.
5. **Notificación y Renderizado:**
   - El nuevo `TipUiState` se emite en el `LiveData`.
   - `MainActivity`, suscrita a dicho `LiveData`, recibe el estado y asigna los textos correspondientes a `textViewPercent`, `textViewTip`, `textViewTotal` y `textViewAmount` sin realizar ninguna operación aritmética ni de formateo.

---

### Dependencias agregadas en Gradle

Para dar soporte oficial a los componentes de arquitectura Jetpack en Java se agregaron en `gradle/libs.versions.toml` y `app/build.gradle.kts`:
- `androidx.lifecycle:lifecycle-viewmodel:2.8.7`
- `androidx.lifecycle:lifecycle-livedata:2.8.7`

Estas dependencias permiten extender de `ViewModel`, utilizar `MutableLiveData` / `LiveData` y desacoplar la retención del estado de los ciclos de vida de la actividad.
