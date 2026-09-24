# Documentación: Refactorización de Welcome App a Clean Architecture

Este documento detalla la evolución de nuestra aplicación desde un enfoque monolítico tradicional (todo en la `MainActivity`) hacia una estructura basada en **Clean Architecture** y **Principios SOLID**, con el fin de eliminar la deuda técnica y hacer el código escalable y mantenible.

---

## 1. Qué se cambió y por qué

### ¿Cómo era antes (Enfoque Tradicional / Monolítico)?
* Todo el código residía en una sola clase (`MainActivity`).
* La vista se encargaba de obtener los recursos directamente de Android (`getResources()`, `getString()`).
* **Problema:** Si la aplicación crecía, la `MainActivity` se convertía en un archivo gigante (*God Object*), la lógica de negocio se mezclaba con el framework de Android y era imposible realizar pruebas unitarias (*unit testing*) sin levantar un emulador.

### ¿Cómo es ahora (Clean Architecture + SOLID)?
* **Separación de responsabilidades:** Dividimos el proyecto en capas estrictas (`domain`, `data`, `presentation`)[cite: 1].
* **Independencia del Framework:** El núcleo del negocio no sabe que está corriendo en Android.
* **Por qué se hizo:** Para aplicar los principios **SOLID** (especialmente Responsabilidad Única e Inversión de Dependencias), facilitar el mantenimiento a largo plazo y evitar la acumulación de deuda técnica desde el inicio del proyecto.

---

## 2. Qué hace cada clase

El proyecto está organizado en paquetes que contienen las siguientes clases clave:

### Capa de Dominio (`domain`) - *El Núcleo del Negocio*
* **`WelcomeInfo.java` (Model):** Entidad de negocio pura. Contiene únicamente las estructuras de datos que el negocio necesita (`message` e `imageResId`), sin dependencias de Android.
* **`WelcomeRepository.java` (Interface):** Contrato que define *qué* datos se necesitan obtener, pero no *cómo* se obtienen (cumple con el principio de Inversión de Dependencias).
* **`GetWelcomeMessageUseCase.java` (Use Case):** Caso de uso que cumple estrictamente con el **Principio de Responsabilidad Única (SRP)**. Su única tarea es invocar al repositorio para traer la información de bienvenida.

### Capa de Datos (`data`) - *Fuentes Externas*
* **`WelcomeRepositoryImpl.java` (Repository Implementation):** Implementa la interfaz `WelcomeRepository` del dominio. Aquí es donde **sí** se permite usar el `Context` de Android para consultar los archivos de recursos (`strings.xml` y `drawable`) y empaquetarlos en la entidad de dominio.

### Capa de Presentación (`presentation`) - *Interfaz de Usuario*
* **`MainActivity.java` (View):** Actúa exclusivamente como la interfaz visual. Inicializa las dependencias de forma limpia, ejecuta el caso de uso y pinta los resultados en los componentes gráficos (`TextView` e `ImageView`).
* **`activity_main.xml` (Layout):** Define la estructura visual utilizando un `LinearLayout` vertical equilibrado y externalizando los textos para soportar internacionalización[cite: 1].

---

## 3. Cómo se conectan las capas

La regla de oro de *Clean Architecture* es que **las dependencias siempre apuntan hacia adentro**. El dominio es el centro y no conoce nada de las capas externas.

El flujo de ejecución de la aplicación funciona de la siguiente manera:

1. **La UI solicita datos:** Cuando la `MainActivity` arranca, necesita mostrar el mensaje de bienvenida.
2. **Llamada al Caso de Uso:** En lugar de buscar los datos por sí misma, la `MainActivity` llama a `GetWelcomeMessageUseCase`.
3. **Consulta al Repositorio:** El Caso de Uso le pide la información a la interfaz `WelcomeRepository`.
4. **Respuesta de la Capa de Datos:** La clase `WelcomeRepositoryImpl` (en la capa de datos) intercepta la petición, utiliza el `Context` para extraer los textos de `strings.xml` y la imagen de `drawable`, y devuelve un objeto de tipo `WelcomeInfo` al dominio.
5. **Renderizado en Pantalla:** El caso de uso le entrega el modelo limpio a la `MainActivity`, la cual se encarga exclusivamente de pintarlo en la interfaz gráfica.

```text
[ Presentation (MainActivity) ] 
       │
       ▼ llama a
[ Domain (Use Case) ] 
       │
       ▼ implementa contrato de
[ Data (Repository Impl) ] ──> (Accede a Android Context / XMLs)