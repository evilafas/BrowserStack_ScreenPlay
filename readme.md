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
| **Healenium** | **3.4.4** |

---

## ⚡ Self-Healing con Healenium

### ¿Qué es Healenium?

**Healenium** es un framework de self-healing que detecta cuando un selector Selenium falla y automáticamente intenta recuperarse usando técnicas de visión por computadora (OpenCV). Esto reduce la fragilidad de los tests y minimiza el mantenimiento de selectores.

**Beneficios:**
- ✅ Recuperación automática de selectores rotos
- ✅ Reduce el mantenimiento de tests
- ✅ Incrementa la confiabilidad de test suites
- ✅ Proporciona métricas de healing para análisis
- ✅ Integración transparente con Selenium

### Requisitos para Healenium

1. **Docker y Docker Compose** instalados
   - [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop)

2. **Espacio en disco:** ~2GB para PostgreSQL + Healenium Backend

3. **Puertos disponibles:** 7878 (Healenium Backend) y 5432 (PostgreSQL)

### Configuración de Healenium

#### 1️⃣ Levantar servicios de Healenium

```bash
# Navega al directorio del proyecto
cd C:\Users\evila\IdeaProjects\BrowserStack_ScreenPlay

# Levanta los servicios en background
docker-compose up -d

# Verifica que estén corriendo
docker-compose ps
```

**Salida esperada:**
```
NAME      COMMAND                  SERVICE   STATUS
healenium "/docker-entrypoint..."  healenium  Up (health: starting)
db        "docker-entrypoint..."   db         Up (healthy)
```

> ⏳ **Nota:** PostgreSQL tarda 10-20 segundos en iniciar. La UI esperará automáticamente.

#### 2️⃣ Verificar disponibilidad

Abre tu navegador en: http://localhost:7878/health

Deberías ver:
```json
{
  "status": "UP"
}
```

#### 3️⃣ Configuración de propiedades

**Archivo:** `src/test/resources/healenium.properties`

```properties
# Número de intentos de recuperación antes de fallar
recovery-tries=1

# Score mínimo de similitud (0.0 - 1.0) para aceptar elemento como válido
# Mayor = más estricto. Recomendado: 0.6 - 0.8
score-cap=0.6

# Habilitar/deshabilitar self-healing globalmente
heal-enabled=true

# Conexión al backend de Healenium
hlm.server.host=localhost
hlm.server.port=7878
```

**Parámetros clave:**

| Parámetro | Rango | Recomendación | Descripción |
|---|---|---|---|
| `recovery-tries` | 1+ | 1-2 | Intentos de recuperación. Más = más lento pero más preciso |
| `score-cap` | 0.0-1.0 | 0.6-0.8 | Score mínimo. Más alto = más exigente |
| `heal-enabled` | true/false | true | Activa/desactiva healing en runtime |
| `hlm.server.host` | hostname | localhost | Host del servidor Healenium |
| `hlm.server.port` | port | 7878 | Puerto del servidor Healenium |

### Ejecutar tests CON Healenium

```bash
# Asegúrate de que Docker Compose está levantado
docker-compose ps

# Ejecuta los tests normalmente
./gradlew clean test
```

**Nota:** Healenium se activa automáticamente porque está configurado en `serenity.properties`:
```ini
webdriver.driver=provided
webdriver.provided.classname=com.browserstack.utils.SelfHealingDriverSource
```

### Ejecutar tests SIN Healenium (fallback)

Si necesitas ejecutar sin self-healing (debugging, etc.):

1. Edita `serenity.properties`:
   ```ini
   webdriver.driver=chrome
   # webdriver.provided.classname=com.browserstack.utils.SelfHealingDriverSource
   ```

2. Ejecuta los tests:
   ```bash
   ./gradlew clean test
   ```

### Cómo funciona el healing

```
1. Se intenta encontrar elemento con el selector original
   ↓
2. Si falla → Healenium captura una screenshot
   ↓
3. Busca un elemento SIMILAR a la captura con OpenCV
   ↓
4. Si encuentra coincidencia con score > score-cap
   → ✅ Healing EXITOSO (usa el nuevo selector)
   
5. Si no encuentra coincidencia
   → ❌ Healing FALLÓ (lanza excepción Selenium original)
```

### Interpretación de logs de Healenium

Durante la ejecución, verás logs como:

```
[INFO] Inicializando WebDriver con capacidad de Self-Healing...
[INFO] WebDriver envuelto con SelfHealingDriver - Self-Healing HABILITADO

[INICIO DEL PRIMER TEST]
[INFO] ═══════════════════════════════════════════════════════
[INFO] PRIMER ESCENARIO - Realizando Health Check de Healenium
[INFO] ═══════════════════════════════════════════════════════
[INFO] Validando configuración...
[INFO] ✓ Configuración válida
[INFO] ✓ Servidor Healenium alcanzable en el intento 1/3

[DURANTE LA EJECUCIÓN]
[INFO] ✓ Healing exitoso | Score: 0.85 | Intento: 1/1

[AL FINAL]
[INFO] ╔════════════════════════════════════════════════════════╗
[INFO] ║          REPORTE DE SELF-HEALING                       ║
[INFO] ║ Total de intentos de healing:                1         ║
[INFO] ║ Heals exitosos:                             1         ║
[INFO] ║ Elementos no recuperables:                  0         ║
[INFO] ║ Score promedio de similitud:            0.85         ║
[INFO] ║ Tasa de éxito:                         100.0%         ║
[INFO] ╚════════════════════════════════════════════════════════╝
```

**Símbolos:**
- `✓` = Éxito
- `✗` = Fallo
- `⚠` = Advertencia
- `❌` = Error crítico

### Quality Gates de Healing

Después de cada escenario, se ejecutan validaciones automáticas:

1. **Success Rate Gate:** Valida que el % de heals exitosos sea > 80%
2. **Average Score Gate:** Valida que el score promedio sea >= score-cap
3. **Recovery Tries Gate:** Valida que no se exceda el límite de intentos

Si algún gate falla:
```
[WARN] ✗ FAIL Quality Gate: Success Rate | Actual: 50.0% | Esperado: 80.0%
```

### Troubleshooting

#### ❌ "No se puede conectar a Healenium Backend"

**Solución:**
```bash
# Verifica que Docker está corriendo
docker ps

# Levanta los servicios
docker-compose up -d

# Espera 20 segundos y reinicia las pruebas
```

#### ❌ "PostgreSQL no inicia"

**Solución:**
```bash
# Elimina datos antiguos
docker-compose down -v

# Levanta nuevamente
docker-compose up -d

# Espera 20+ segundos
```

#### ❌ "El puerto 7878 ya está en uso"

**Solución:**
```bash
# Busca qué está usando el puerto
netstat -ano | findstr :7878

# Detén el proceso o cambia el puerto en docker-compose.yml
docker-compose down
```

#### ❌ "Score muy bajo (healing inefectivo)"

**Solución:**
- Aumenta `recovery-tries` en `healenium.properties` (ej: 2-3)
- Baja `score-cap` si es necesario (ej: 0.5)
- Revisa que los selectores sean robustos (ID > CSS > XPath)
- Mejora los selectores CSS/XPath para mayor estabilidad

#### ⚠️ "Healing desactivado / heal-enabled=false"

**Verificar:**
```bash
# Abre healenium.properties y asegúrate de:
heal-enabled=true

# También verifica serenity.properties:
webdriver.driver=provided
webdriver.provided.classname=com.browserstack.utils.SelfHealingDriverSource
```

### Dashboard de Healenium (Opcional)

Healenium proporciona un dashboard web (no incluido en la imagen Docker por defecto). Si quieres acceder a métricas detalladas:

```bash
# Accede a la API REST de Healenium
curl http://localhost:7878/api/healing/stats
```

### Detener servicios de Healenium

```bash
# Detiene los contenedores (mantiene datos)
docker-compose stop

# Detiene y elimina contenedores (limpia datos)
docker-compose down

# Detiene, elimina y limpia volúmenes (reinicia desde cero)
docker-compose down -v
```

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
