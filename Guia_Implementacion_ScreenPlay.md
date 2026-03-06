# Guía de Implementación: Framework de Automatización con Serenity BDD + Healenium

## 1. Visión General del Proyecto

Este framework automatiza pruebas E2E sobre [bstackdemo.com](https://bstackdemo.com) usando el **patrón Screenplay** de Serenity BDD, con capacidad de **self-healing** (auto-reparación de selectores) mediante Healenium. Está escrito en Java 21 con Gradle y usa Cucumber con Gherkin en español.

**¿Por qué esta combinación?**
- **Serenity BDD**: genera reportes HTML detallados con screenshots por paso, ideal para documentar ejecuciones.
- **Screenplay Pattern**: separa responsabilidades en Tasks, Interactions y Questions, haciendo los tests más mantenibles que el clásico Page Object.
- **Healenium**: cuando un selector CSS/XPath se rompe (por cambios en el DOM), intenta encontrar el elemento usando similitud visual/estructural, evitando falsos negativos.
- **Cucumber en español**: permite que perfiles no técnicos lean y entiendan los escenarios.

---

## 2. Estructura del Proyecto

```
BrowserStack_ScreenPlay/
├── build.gradle                    # Dependencias y configuración de build
├── gradle.properties               # Versiones centralizadas
├── serenity.properties             # Config de Serenity (driver, screenshots, reportes)
├── docker-compose.yml              # Infraestructura Healenium (PostgreSQL + backend)
├── src/
│   ├── main/java/com/browserstack/
│   │   ├── base/                   # Clase base abstracta para tests
│   │   ├── config/                 # Configuración de Healenium y quality gates
│   │   ├── exceptions/             # Excepciones personalizadas del dominio
│   │   ├── interactions/           # Acciones atómicas (click, filtrar)
│   │   ├── listeners/              # Listeners de Serenity y JUnit
│   │   ├── models/                 # POJOs de datos (UserData)
│   │   ├── questions/              # Verificaciones (obtener texto de elemento)
│   │   ├── tasks/                  # Tareas de negocio (Login, Checkout)
│   │   ├── userInterfaces/         # Localizadores de UI (Targets)
│   │   └── utils/                  # Utilidades (ExcelReader, SelfHealingDriverSource)
│   ├── main/resources/
│   │   └── META-INF/services/      # Registro automático del listener via ServiceLoader
│   └── test/
│       ├── java/
│       │   ├── runners/            # Runners de Cucumber
│       │   └── stepDefinitions/    # Implementación de pasos Gherkin
│       └── resources/
│           ├── features/           # Archivos .feature en español
│           ├── data/               # Datos de prueba (Credentials.xlsx)
│           └── healenium.properties
```

---

## 3. Implementación Paso a Paso

### 3.1 Configurar el Build (`build.gradle` + `gradle.properties`)

**`gradle.properties`** centraliza las versiones para evitar inconsistencias:

```properties
serenityVersion=3.7.1
healeniumVersion=3.4.4
poiVersion=5.2.3
webdrivermanagerVersion=5.6.3
lombokVersion=1.18.30
```

**`build.gradle`** — Lo esencial:

```gradle
plugins {
    id 'java-library'
    id 'net.serenity-bdd.serenity-gradle-plugin' version "${serenityVersion}"
}

sourceCompatibility = 21
targetCompatibility = 21

repositories { mavenCentral() }

dependencies {
    // Core Serenity + Cucumber
    implementation "net.serenity-bdd:serenity-core:${serenityVersion}"
    implementation "net.serenity-bdd:serenity-screenplay:${serenityVersion}"
    implementation "net.serenity-bdd:serenity-screenplay-webdriver:${serenityVersion}"
    implementation "net.serenity-bdd:serenity-cucumber:${serenityVersion}"

    // Self-healing
    implementation "com.epam.healenium:healenium-web:${healeniumVersion}"

    // Datos de prueba desde Excel
    implementation "org.apache.poi:poi-ooxml:${poiVersion}"

    // Gestión automática de drivers
    implementation "io.github.bonigarcia:webdrivermanager:${webdrivermanagerVersion}"

    // Utilidades
    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
    implementation "org.assertj:assertj-core:3.24.2"
}

test {
    useJUnit()
    // Necesario para Java 21 — abre módulos internos al framework
    jvmArgs += ['--add-opens', 'java.base/java.lang=ALL-UNNAMED',
                '--add-opens', 'java.base/java.io=ALL-UNNAMED']
    finalizedBy 'aggregate'  // Genera reporte Serenity al terminar
}
```

**¿Por qué `jvmArgs`?** Java 21 tiene módulos cerrados por defecto. Serenity y Healenium usan reflexión internamente, así que necesitan acceso explícito.

**¿Por qué `finalizedBy 'aggregate'`?** Esto ejecuta automáticamente la generación del reporte HTML de Serenity después de los tests.

---

### 3.2 Configurar Serenity (`serenity.properties`)

```properties
# Usa un driver "provided" — nosotros controlamos la creación (para inyectar Healenium)
webdriver.driver=provided
webdriver.provided.classname=com.browserstack.utils.SelfHealingDriverSource

# URL base de la app bajo prueba
webdriver.base.url=https://bstackdemo.com/

# Captura screenshot en cada acción (útil para debugging y reportes)
serenity.take.screenshots=FOR_EACH_ACTION
serenity.report.show.step.details=true
serenity.outputDirectory=target/site/serenity
```

**¿Por qué `webdriver.driver=provided`?** En lugar de dejar que Serenity cree el driver, lo creamos nosotros para envolverlo con Healenium. La clase indicada en `webdriver.provided.classname` actúa como factory.

---

### 3.3 Crear el Driver con Self-Healing (`SelfHealingDriverSource`)

```java
public class SelfHealingDriverSource implements DriverSource {

    @Override
    public WebDriver newDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--disable-notifications");

        WebDriver delegate = new ChromeDriver(options);

        // Configuración inline de Healenium (conexión al backend Docker)
        Config config = ConfigFactory.parseString(
            "recovery-tries=1\n" +
            "score-cap=0.6\n" +
            "heal-enabled=true\n" +
            "hlm.server.url=http://localhost:7878\n" +
            "hlm.imitator.url=http://localhost:8000"
        );

        // Envuelve el driver real con capacidad de self-healing
        return SelfHealingDriver.create(delegate, config);
    }

    @Override
    public boolean takesScreenshots() { return true; }
}
```

**¿Cómo funciona?** Cuando un `findElement()` falla con el selector original, Healenium consulta su backend (que tiene historial de selectores previos) y usa algoritmos de similitud para encontrar el elemento correcto. Si lo encuentra con score >= 0.6 (60% de similitud), lo usa automáticamente.

---

### 3.4 Infraestructura Docker para Healenium

```yaml
# docker-compose.yml
services:
  healenium-db:
    image: postgres:14-alpine
    ports: ["5432:5432"]
    environment:
      POSTGRES_DB: healenium
      POSTGRES_USER: healenium_user
      POSTGRES_PASSWORD: YDk2nmNs4s9aCP6K
    volumes:
      - ./docker/postgres-init:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U healenium_user -d healenium"]

  hlm-selector-imitator:
    image: healenium/hlm-selector-imitator:1
    ports: ["8000:8000"]

  healenium:
    image: healenium/hlm-backend:3.4.4
    ports: ["7878:7878"]
    depends_on:
      healenium-db: { condition: service_healthy }
    environment:
      SPRING_POSTGRES_DB: healenium
      SPRING_POSTGRES_SCHEMA: healenium
      SPRING_POSTGRES_USER: healenium_user
      SPRING_POSTGRES_PASSWORD: YDk2nmNs4s9aCP6K
      KEY_SELECTOR_URL: "false"
      COLLECT_METRICS: "true"
```

**¿Por qué 3 servicios?**
- **PostgreSQL**: almacena el historial de selectores y sus estados. Así Healenium "recuerda" cómo era un elemento antes.
- **hlm-selector-imitator**: servicio de visión por computadora que compara capturas del DOM.
- **healenium backend**: coordina todo, expone la API REST que consulta el driver.

**Levantar:** `docker-compose up -d` (debe estar corriendo ANTES de ejecutar tests).

---

### 3.5 Configuración y Health Check de Healenium

**`HealeniumConfig.java`** — Singleton que carga `healenium.properties`:

```java
public class HealeniumConfig {
    private static HealeniumConfig instance;
    private final Properties properties;

    private HealeniumConfig() {
        properties = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("healenium.properties")) {
            properties.load(is);
        }
    }

    public static synchronized HealeniumConfig getInstance() {
        if (instance == null) instance = new HealeniumConfig();
        return instance;
    }

    public boolean isHealEnabled() { return Boolean.parseBoolean(get("heal-enabled")); }
    public double getScoreCap() { return Double.parseDouble(get("score-cap")); }
    // ... más getters
}
```

**`HealeniumHealthCheck.java`** — Valida antes de cada suite que Docker esté levantado:

```java
public class HealeniumHealthCheck {
    public static boolean isServerReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 7878), 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
```

**¿Por qué un health check?** Si Docker no está corriendo, los tests fallarían con errores crípticos de conexión. El health check da un mensaje claro: "Healenium backend no disponible".

---

### 3.6 Quality Gate de Healing

`HealingQualityGate.java` recopila métricas durante la ejecución y al final evalúa:

```java
public class HealingQualityGate {
    private final AtomicInteger totalAttempts = new AtomicInteger(0);
    private final AtomicInteger successfulHeals = new AtomicInteger(0);
    private final List<String> unrecoverableElements = new ArrayList<>();

    public void recordHealingAttempt(boolean success, double score, String element) {
        totalAttempts.incrementAndGet();
        if (success) successfulHeals.incrementAndGet();
        else unrecoverableElements.add(element);
    }

    public boolean passesQualityGate() {
        double successRate = (double) successfulHeals.get() / totalAttempts.get();
        return successRate >= 0.8;  // Mínimo 80% de éxito en healing
    }
}
```

**¿Para qué sirve?** Si muchos elementos no se pueden "curar", es señal de que el DOM cambió demasiado y los tests necesitan actualización manual. El quality gate lo detecta.

---

### 3.7 El Patrón Screenplay

Este es el corazón del framework. Separa las pruebas en 4 conceptos:

#### Actors (implícito en Serenity)
El "actor" es quien ejecuta acciones. Serenity lo gestiona automáticamente.

#### Tasks — Acciones de negocio

```java
public class LoginTask implements Task {
    @Override
    @Step("{0} inicia sesión")
    public <T extends Actor> void performAs(T actor) {
        UserData user = ExcelReader.readData();
        actor.attemptsTo(
            Click.on(LoginPageUI.USERNAME_FIELD),
            SendKeys.of(user.getUserName()).into(LoginPageUI.USERNAME_FIELD),
            Click.on(LoginPageUI.PASSWORD_FIELD),
            SendKeys.of(user.getPassword()).into(LoginPageUI.PASSWORD_FIELD),
            Click.on(LoginPageUI.LOGIN_BUTTON)
        );
    }

    public static LoginTask login() { return Tasks.instrumented(LoginTask.class); }
}
```

**¿Por qué Tasks?** Encapsulan flujos de negocio completos. Un step definition solo dice `actor.attemptsTo(LoginTask.login())` — limpio y reutilizable.

#### Interactions — Acciones atómicas

```java
public class FiltrarPorMarca implements Task {
    private final String marca;

    public FiltrarPorMarca(String marca) { this.marca = marca; }

    @Override
    @Step("{0} filtra por marca {1}")
    public <T extends Actor> void performAs(T actor) {
        Target MARCA_CHECKBOX = Target.the("checkbox de " + marca)
            .locatedBy("//span[text()='" + marca + "']");
        actor.attemptsTo(Click.on(MARCA_CHECKBOX));
    }

    public static FiltrarPorMarca porMarca(String marca) {
        return Tasks.instrumented(FiltrarPorMarca.class, marca);
    }
}
```

**¿Por qué separar Interactions de Tasks?** Las interactions son reutilizables en múltiples tasks. "Filtrar por marca" puede usarse en tests de compra, de búsqueda, etc.

#### Questions — Verificaciones

```java
public class TextOfElement implements Question<String> {
    private final Target target;

    public TextOfElement(Target target) { this.target = target; }

    @Override
    public String answeredBy(Actor actor) {
        return target.resolveFor(actor).getText();
    }

    public static TextOfElement of(Target target) {
        return new TextOfElement(target);
    }
}
```

**¿Por qué Questions?** Permiten hacer aserciones declarativas: `actor.should(seeThat(TextOfElement.of(HOME.USERNAME), equalTo("demouser")))`.

#### User Interfaces — Localizadores

```java
public class LoginPageUI {
    public static final Target USERNAME_FIELD =
        Target.the("campo de usuario").locatedBy("#username");
    public static final Target PASSWORD_FIELD =
        Target.the("campo de contraseña").locatedBy("#password");
    public static final Target LOGIN_BUTTON =
        Target.the("botón de login").locatedBy("#login-btn");
}
```

**¿Por qué no Page Objects?** En Screenplay, los localizadores se centralizan en clases UI estáticas. No hay lógica de interacción aquí — solo definiciones de "dónde está cada elemento".

---

### 3.8 Modelo de Datos y Lectura desde Excel

**`UserData.java`** — POJO con Lombok:

```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserData {
    private String userName, password, firstName, lastName;
    private String address, state, postalCode;
}
```

**`ExcelReader.java`** — Lee `Credentials.xlsx` con Apache POI:

```java
public class ExcelReader {
    private static final String FILE_PATH = "src/test/resources/data/Credentials.xlsx";

    public static UserData readData() {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1); // Fila 1 (0 es header)
            return UserData.builder()
                .userName(row.getCell(0).getStringCellValue())
                .password(row.getCell(1).getStringCellValue())
                .firstName(row.getCell(2).getStringCellValue())
                // ... demás campos
                .build();
        }
    }
}
```

**¿Por qué Excel?** Permite que personas no técnicas modifiquen datos de prueba sin tocar código. También facilita data-driven testing con múltiples filas.

---

### 3.9 Features en Gherkin (Español)

**`login.feature`:**

```gherkin
# language: es
@Login
Característica: Iniciar sesión en BStackDemo

  @Exitoso
  Escenario: Login exitoso con credenciales válidas
    Dado que el usuario abre la página de BStackDemo
    Cuando el usuario inicia sesión con credenciales válidas
    Entonces debería ver el nombre de usuario "demouser"
```

**`compra_producto.feature`:**

```gherkin
# language: es
@Compra
Característica: Compra de producto

  @Exitoso
  Escenario: Compra exitosa de producto filtrado por marca
    Dado que el usuario se autentica en BStackDemo
    Cuando filtra por la marca "Samsung"
    Y agrega el primer producto al carrito
    Y completa el proceso de checkout
    Entonces debería ver el mensaje de confirmación de orden
```

**¿Por qué `# language: es`?** Activa los keywords de Cucumber en español (Dado/Cuando/Entonces en lugar de Given/When/Then).

---

### 3.10 Step Definitions

```java
public class LoginStepDefinitions {
    Actor actor;

    @Dado("que el usuario abre la página de BStackDemo")
    public void abrirPagina() {
        actor = OnStage.theActorCalled("usuario");
        actor.attemptsTo(OpenWebTask.openWeb());
    }

    @Cuando("el usuario inicia sesión con credenciales válidas")
    public void login() {
        actor.attemptsTo(LoginTask.login());
    }

    @Entonces("debería ver el nombre de usuario {string}")
    public void validarUsuario(String expected) {
        actor.should(
            seeThat(TextOfElement.of(HomePageUI.USERNAME_DISPLAY),
                    equalTo(expected))
        );
    }
}
```

---

### 3.11 Hooks (Configuración Pre/Post Test)

```java
public class WebDriverHooks {
    private static boolean healthCheckDone = false;

    @Before(order = 0)
    public void healthCheck() {
        if (!healthCheckDone) {
            if (!HealeniumHealthCheck.isServerReachable()) {
                throw new RuntimeException("Healenium backend no disponible. ¿Docker está corriendo?");
            }
            healthCheckDone = true;
        }
    }

    @Before(order = 1)
    public void setStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @After(order = 0)
    public void generateHealingReport() {
        HealingQualityGate gate = HealingQualityGate.getInstance();
        gate.generateReport();  // Log de métricas de healing
        gate.reset();
    }
}
```

**¿Por qué `order`?** Controla la secuencia: primero el health check (order=0), luego el stage (order=1). En `@After`, order=0 se ejecuta al final.

---

### 3.12 Listeners

**`HealeniumSerenityListener`** se registra automáticamente via `META-INF/services/net.thucydides.core.steps.StepListener`:

```
com.browserstack.listeners.HealeniumSerenityListener
```

Este listener intercepta pasos de Serenity para registrar métricas de healing en el `HealingQualityGate`. Se registra sin código explícito gracias al mecanismo ServiceLoader de Java.

---

### 3.13 Runners de Cucumber

```java
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features/login.feature",
    glue = "stepDefinitions"
)
public class LoginRunner {}
```

Cada runner apunta a un `.feature` específico. `glue` indica el paquete donde están los step definitions.

---

## 4. Ejecución

```bash
# 1. Levantar infraestructura Healenium
docker-compose up -d

# 2. Ejecutar todos los tests
./gradlew clean test

# 3. Ejecutar por tag
./gradlew clean test -Dcucumber.filter.tags="@Login"
./gradlew clean test -Dcucumber.filter.tags="@Compra"

# 4. Ver reporte
# Abrir: target/site/serenity/index.html
```

---

## 5. Flujo de Ejecución Completo

```
1. Gradle ejecuta el Runner
2. @Before(0): Health check → ¿Docker OK?
3. @Before(1): Prepara el escenario (OnlineCast)
4. SelfHealingDriverSource crea ChromeDriver + Healenium wrapper
5. Steps ejecutan Tasks → Interactions (actor.attemptsTo)
6. Cada findElement pasa por Healenium:
   - Selector funciona → ejecución normal
   - Selector falla → Healenium busca alternativa (score >= 0.6)
   - No encuentra nada → ElementNotFoundException
7. Questions validan resultados (actor.should)
8. @After(0): Genera reporte de healing + reset métricas
9. Serenity 'aggregate' genera reporte HTML final
```

---

## 6. Resumen de Dependencias Clave

| Componente | Versión | Propósito |
|---|---|---|
| Java | 21 | Lenguaje base |
| Gradle | 8.5 | Build y gestión de dependencias |
| Serenity BDD | 3.7.1 | Framework BDD + reportes + Screenplay |
| Cucumber | 3.7.1 | Soporte Gherkin |
| Healenium | 3.4.4 | Self-healing de selectores |
| WebDriverManager | 5.6.3 | Descarga automática de ChromeDriver |
| Apache POI | 5.2.3 | Lectura de Excel |
| Lombok | 1.18.30 | Reducir boilerplate en POJOs |
| PostgreSQL | 14 | Base de datos de Healenium |
