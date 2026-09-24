# Refactorización del proyecto

## Qué se cambió y por qué

Antes de la refactorización, las pantallas consultaban el `ContentProvider` mediante `CursorLoader` y trabajaban directamente con `Cursor`, `Uri` y las columnas de SQLite. La pantalla de edición también construía `ContentValues` y escribía en el proveedor. Esto hacía que la interfaz conociera detalles de almacenamiento y que cambiar la persistencia implicara modificar fragments y adapters.

El proyecto se separó en cinco módulos Gradle: `app`, `presentation`, `application`, `domain` e `infrastructure`. `app` contiene `MainActivity` y `AddressBookCompositionRoot`; la interfaz reutilizable y sus recursos viven en `presentation`; las operaciones y contratos están en `application`; el modelo y sus reglas de validación están en `domain`; y el proveedor, SQLite y la implementación del repositorio están en `infrastructure`.

La persistencia se oculta tras `ContactRepository`. `ContentResolverContactRepository` implementa ese contrato usando el `ContentResolver` y convierte filas `Cursor` en entidades `Contact`. Las pantallas trabajan con identificadores y objetos de dominio mediante casos de uso; ya no construyen URI ni conocen nombres de tablas o columnas.

La composición de dependencias está en `AddressBookCompositionRoot`, dentro de `app`. Esta clase extiende `Application`, construye el repositorio concreto y expone los casos de uso mediante el contrato `AppDependencies`. La `MainActivity` permanece en `app` como punto de coordinación y navegación, mientras que fragments, adapter y recursos se mantienen en el módulo `presentation`.

Se mantuvieron el `applicationId`, la base de datos y el esquema SQLite existentes, así como los flujos de listar, crear, editar y eliminar contactos. La migración reorganiza el código y el acceso a datos sin cambiar la identidad instalada de la aplicación ni requerir borrar los contactos guardados.

## Qué hace cada clase

| Clase y ubicación | Qué hace ahora | Por qué quedó así |
| --- | --- | --- |
| `MainActivity` — `app` | Configura la Activity, coordina la navegación entre lista, detalle y formulario, y responde a los callbacks de las pantallas. Pasa identificadores de contacto entre pantallas. | Es el punto de entrada declarado en el manifest y coordina navegación Android. No accede al repositorio ni al esquema SQLite. |
| `AddressBookCompositionRoot` — `app` | Extiende `Application`, crea `ContentResolverContactRepository` y proporciona los casos de uso mediante `AppDependencies`. | Centraliza la creación de implementaciones concretas en el borde de la aplicación. La interfaz y los casos de uso no construyen su propio repositorio. |
| `AppDependencies` — `application.contracts` | Expone fábricas para los casos de uso de crear, actualizar, eliminar, listar y consultar un contacto. | Permite que la presentación obtenga las operaciones que necesita sin depender de `infrastructure` ni de `AddressBookCompositionRoot`. |
| `ContactRepository` — `application.contracts.repositories` | Define `create`, `update`, `delete`, `getById` y `getAll`, usando la entidad `Contact` y tipos Java estándar. | Es el límite de persistencia requerido por la aplicación. Sus firmas no exponen `Cursor`, `Uri`, `ContentValues` ni clases Android. |
| `CreateContactUseCase` — `application.usecases` | Valida el contacto mediante las reglas del dominio y solicita su creación al repositorio. Devuelve el identificador generado. | Concentra la operación de creación y garantiza que las reglas se apliquen independientemente de qué pantalla la invoque. |
| `UpdateContactUseCase` — `application.usecases` | Comprueba que el contacto tenga identificador válido, valida sus datos y delega la actualización. | Mantiene la coordinación y las precondiciones de edición fuera de la interfaz. |
| `DeleteContactUseCase` — `application.usecases` | Comprueba el identificador y delega la eliminación al repositorio. | Evita que una pantalla tenga que conocer cómo se borra un registro. |
| `GetAllContactsUseCase` — `application.usecases` | Solicita al repositorio la lista de contactos. | Proporciona una operación de lectura explícita para la pantalla de lista. |
| `GetContactByIdUseCase` — `application.usecases` | Recupera un contacto por su identificador. | Separa la consulta de detalle y edición del mecanismo utilizado para obtener los datos. |
| `Contact` — `domain.entities` | Representa un contacto con identificador, nombre, teléfono, correo, dirección, ciudad, provincia y código postal. Normaliza valores nulos a cadenas vacías. | Es el modelo compartido por aplicación e infraestructura; no depende de Android ni de SQLite. |
| `ContactValidator` — `domain.entities` | Contiene las reglas reutilizables para nombre obligatorio y formatos aceptados de teléfono, correo y código postal. | Las reglas de negocio pertenecen al dominio y se pueden aplicar tanto en la UI como en los casos de uso. |
| `ContactsFragment` — `presentation` | Solicita la lista mediante `GetAllContactsUseCase`, carga los datos fuera del hilo de interfaz y los entrega al adapter. | Presenta la lista sin consultar cursores ni importar clases de infraestructura. |
| `ContactsAdapter` — `presentation` | Representa una lista de entidades `Contact` y comunica el identificador seleccionado. | Solo enlaza datos de dominio con las filas del RecyclerView; no conoce la base de datos. |
| `DetailFragment` — `presentation` | Carga un contacto con `GetContactByIdUseCase` y solicita su eliminación mediante `DeleteContactUseCase` tras la confirmación. | Conserva la interacción y presentación del detalle sin acceso directo al proveedor. |
| `AddEditFragment` — `presentation` | En edición carga el contacto por identificador; al guardar llama a `CreateContactUseCase` o `UpdateContactUseCase`. Muestra mensajes de éxito o error y las validaciones de campo. | Coordina la interfaz y delega las reglas definitivas y la persistencia hacia las capas interiores. |
| `ItemDivider` — `presentation` | Dibuja el separador visual entre elementos del RecyclerView. | Aísla un detalle gráfico de la Activity y del adapter. |
| `ContentResolverContactRepository` — `infrastructure.repositories` | Implementa `ContactRepository`; convierte contactos a `ContentValues`, ejecuta consultas y convierte los cursores recibidos en entidades `Contact`. | Traduce el contrato independiente de almacenamiento a la API Android que usa el proveedor. |
| `AddressBookContentProvider` — `infrastructure.persistence` | Atiende consultas, inserciones, cambios y eliminaciones para la tabla de contactos, y notifica los cambios de contenido. | Encapsula la interfaz Android de acceso a la base de datos. Solo infraestructura conoce sus URI y operaciones. |
| `AddressBookDatabaseHelper` — `infrastructure.persistence` | Crea la base SQLite `AddressBook.db` y la tabla de contactos. | Mantiene el esquema y ciclo de vida de SQLite fuera de las capas de negocio y presentación. |
| `DatabaseDescription` — `infrastructure.persistence` | Define la autoridad, URI, tabla, columnas y construcción de URI por identificador. | Agrupa los detalles del esquema y del proveedor en infraestructura. |

## Cómo se conectan las capas

Al iniciar, Android crea `AddressBookCompositionRoot`. Esta instancia construye el repositorio concreto. Las pantallas obtienen `AppDependencies` desde la aplicación y solicitan los casos de uso correspondientes.

```text
MainActivity (app)
    → fragments y adapter (presentation)
        → casos de uso (application)
            → ContactRepository (contrato)
                ← ContentResolverContactRepository (infrastructure)
                    → AddressBookContentProvider
                        → SQLite

Contact y ContactValidator (domain)
    ← usados por application, presentation e infrastructure
```

Al listar, `ContactsFragment` llama a `GetAllContactsUseCase`; el repositorio consulta el proveedor, convierte cada fila a `Contact` y devuelve la lista para mostrarla. Para abrir o editar, la navegación pasa el identificador, y `GetContactByIdUseCase` obtiene la entidad. Al guardar, `AddEditFragment` crea la entidad y llama al caso de uso de creación o actualización. Al borrar, `DetailFragment` solicita confirmación y luego invoca el caso de uso de eliminación.

Las lecturas y escrituras de las pantallas se ejecutan fuera del hilo principal. La presentación muestra los resultados en el hilo de UI y no procesa `Cursor` ni gestiona recursos de base de datos.

## Separación de Gradle

| Archivo / módulo | Qué configura | Dependencias principales |
| --- | --- | --- |
| `settings.gradle.kts` | Nombre del proyecto, repositorios y módulos incluidos. | Incluye `:app`, `:presentation`, `:application`, `:domain` e `:infrastructure`. |
| `build.gradle.kts` raíz | Declara los alias de plugins Android sin aplicarlos al proyecto raíz. | `com.android.application` y `com.android.library`. |
| `gradle/libs.versions.toml` | Centraliza versiones de AGP y bibliotecas Android, y los alias de plugins y dependencias. | AppCompat, Fragment, Material, RecyclerView, CoordinatorLayout, ConstraintLayout y librerías de pruebas. |
| `app/build.gradle.kts` | Configura el APK, `applicationId`, SDK, versión y compatibilidad Java 11. | `presentation`, `infrastructure`, `application`, AppCompat y Fragment. |
| `presentation/build.gradle.kts` | Configura una biblioteca Android para fragments, adapter y recursos de interfaz. | `application`, `domain` y las bibliotecas Android de interfaz que utiliza. |
| `application/build.gradle.kts` | Configura una biblioteca Java con compatibilidad Java 11. | `api(project(":domain"))`, porque las firmas públicas usan la entidad del dominio. |
| `domain/build.gradle.kts` | Configura una biblioteca Java con compatibilidad Java 11. | No depende de otros módulos ni de Android. |
| `infrastructure/build.gradle.kts` | Configura una biblioteca Android con compatibilidad Java 11 y el proveedor en su manifest. | `application` y `domain`; implementa el contrato de repositorio usando Android y SQLite. |

La dirección de dependencias entre módulos es:

```text
:app ───────────────→ :presentation
  ├─────────────────→ :infrastructure
  └─────────────────→ :application

:presentation ──────→ :application ──────→ :domain
       └────────────→ :domain

:infrastructure ───→ :application
       └────────────→ :domain
```

`app` conecta las implementaciones en el composition root. `domain` queda independiente; `application` no conoce Android; `presentation` no depende de infraestructura; e infraestructura implementa el contrato definido por aplicación.

Los recursos visuales (layouts, menú, temas, estilos, colores, dimensiones e iconos) están en `presentation/src/main/res`. El manifest de `app` declara `AddressBookCompositionRoot` y `MainActivity`; el manifest de `infrastructure` registra `AddressBookContentProvider`. Gradle combina ambos manifests y los recursos de la biblioteca de presentación al generar el APK.

