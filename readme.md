# BrowserStack ScreenPlay

Proyecto de automatización de pruebas E2E sobre [bstackdemo.com](https://bstackdemo.com), alineado al **Gobierno de Automatización de Pruebas de Telefónica**.

---

## Stack tecnológico

| Herramienta | Versión |
|---|---|
| Java | 21 |
| Gradle | 8.5 |
| Serenity BDD | 3.7.1 |
| Serenity Gradle Plugin | 3.3.0 |
| Cucumber | (vía serenity-cucumber 3.7.1) |
| JUnit | 4 |
| Apache POI | 5.2.3 |
| WebDriverManager (bonigarcia) | 5.6.3 |
| Lombok | 1.18.30 |
| AssertJ | 3.24.2 |

---

## Requisitos previos

- **Java 21** instalado y configurado en `JAVA_HOME`
- **Google Chrome** instalado (ChromeDriver se descarga automáticamente)
- **Gradle** no es necesario tenerlo instalado globalmente — el proyecto incluye Gradle Wrapper

---

## Estructura del proyecto

```
BrowserStack_ScreenPlay/
├── src/
│   ├── main/java/com/browserstack/
│   │   ├── exceptions/          # Excepciones de dominio (DataReadException, ElementNotFoundException, ApplicationOpenException)
│   │   ├── interactions/        # Interacciones atómicas (AddProductToCart, FiltrarPorMarca)
│   │   ├── models/              # Modelos de datos (UserData)
│   │   ├── questions/           # Preguntas del actor (TextOfElement)
│   │   ├── tasks/               # Tareas de negocio (LoginTask, CheckoutTask, OpenWebTask)
│   │   ├── userInterfaces/      # Targets de la UI (LoginPageUI, HomePageUI, CheckoutPageUI)
│   │   └── utils/               # Utilidades (ExcelReader)
│   └── test/
│       ├── java/
│       │   ├── runners/         # Runners de Cucumber (LoginRunner, CompraProductoRunner)
│       │   └── stepDefinitions/ # Definiciones de pasos (LoginStepDefinitions, CompraStepDefinitions, WebDriverHooks)
│       └── resources/
│           ├── features/        # Escenarios Gherkin en español (login.feature, compra_producto.feature)
│           └── data/
│               └── Credentials.xlsx   # Datos de prueba externos
├── build.gradle
├── gradle.properties            # Versiones centralizadas de dependencias
└── serenity.properties          # Configuración de Serenity BDD
```

---

## Patrón Screenplay

El proyecto implementa el patrón **Screenplay** de Serenity BDD con el mapeo obligatorio del gobierno:

| Paso Gherkin | Método del Actor | Propósito |
|---|---|---|
| `@Dado` / `Given` | `wasAbleTo()` | Precondición — estado inicial |
| `@Cuando` / `When` | `attemptsTo()` | Acción bajo prueba |
| `@Entonces` / `Then` | `should()` | Validación / aserción |

---

## Datos de prueba

Los datos de prueba se gestionan externamente en un archivo Excel, sin datos hardcodeados en el código.

**Ruta:** `src/test/resources/data/Credentials.xlsx`

| Columna | Campo |
|---|---|
| 0 | userName |
| 1 | password |
| 2 | firstName |
| 3 | lastName |
| 4 | address |
| 5 | state |
| 6 | postalCode |

> La fila 0 es la cabecera. Los datos se leen desde la fila 1.

---

## Ejecución

### Ejecutar todos los tests

```bash
./gradlew clean test
```

### Ejecutar por tag

```bash
./gradlew clean test -Dcucumber.filter.tags="@Login"
./gradlew clean test -Dcucumber.filter.tags="@Compra"
```

### Ver reporte Serenity

Después de ejecutar, el reporte HTML se genera en:

```
target/site/serenity/index.html
```

También se puede abrir desde la salida del build:
```
file:///C:/ruta/al/proyecto/target/site/serenity/index.html
```

---

## Escenarios automatizados

### Login (`@Login @Exitoso`)
- Navegar a bstackdemo.com
- Autenticarse con credenciales válidas
- Verificar que el nombre de usuario es visible en pantalla

### Compra de producto (`@Compra @Exitoso`)
- Autenticarse en la aplicación
- Filtrar productos por marca
- Agregar un producto al carrito
- Completar el proceso de checkout con datos de envío
- Verificar confirmación de orden

---

## Convenciones de código (Gobierno Telefónica)

- **Clases:** `PascalCase`
- **Métodos:** `camelCase`
- **Targets UI:** `UPPER_SNAKE_CASE` con prefijos semánticos:
  - `BTN_` → botones
  - `TXT_` → campos de texto / etiquetas de texto
  - `LBL_` → labels
  - `INPUT_` → inputs de formulario
- **Escenarios:** todos deben tener al menos un `@Tag`
- **Idioma Gherkin:** español (`#language: es`)
- **Datos:** siempre externos al código (Excel u otro origen)
