package com.browserstack.config;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilidad centralizada para logging de eventos de Healenium.
 *
 * Proporciona métodos de logging estructurados con formatos consistentes
 * para eventos de self-healing, facilitando la lectura y análisis de logs.
 */
@Slf4j
public class HealeniumLogger {

    private static final String BOX_CORNER = "╔";
    private static final String BOX_CORNER_END = "╗";
    private static final String BOX_SIDE = "║";
    private static final String BOX_BOTTOM = "╚";
    private static final String BOX_BOTTOM_END = "╝";
    private static final String BOX_LINE = "═";
    private static final String SUCCESS = "✓";
    private static final String FAIL = "✗";
    private static final String WARN = "⚠";
    private static final String ERROR = "❌";

    /**
     * Registra el inicio de un health check.
     */
    public static void logHealthCheckStart() {
        log.info("{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}",
            BOX_CORNER, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_CORNER_END);
        log.info("{}       VERIFICANDO SALUD DE HEALENIUM (HEALTH CHECK)    {}", BOX_SIDE, BOX_SIDE);
        log.info("{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}{}",
            BOX_BOTTOM, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE,
            BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_LINE, BOX_BOTTOM_END);
    }

    /**
     * Registra la inicialización exitosa de SelfHealingDriver.
     */
    public static void logSelfHealingDriverInitialized() {
        log.info("{} Inicializando WebDriver con capacidad de Self-Healing...", SUCCESS);
        log.info("{} WebDriver envuelto con SelfHealingDriver - Self-Healing HABILITADO", SUCCESS);
    }

    /**
     * Registra el inicio de un escenario.
     */
    public static void logScenarioStart(String scenarioName) {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║ Iniciando escenario: {}", scenarioName);
        log.info("╚════════════════════════════════════════════════════════╝");
    }

    /**
     * Registra el fin exitoso de un escenario.
     */
    public static void logScenarioSuccess(String scenarioName) {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║ Escenario completado: {} {}", scenarioName, SUCCESS);
        log.info("╚════════════════════════════════════════════════════════╝");
    }

    /**
     * Registra el fin fallido de un escenario.
     */
    public static void logScenarioFailed(String scenarioName) {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║ Escenario completado: {} {}", scenarioName, ERROR);
        log.info("╚════════════════════════════════════════════════════════╝");
    }

    /**
     * Registra un healing exitoso con score.
     */
    public static void logHealingSuccess(double score, int attempt, int totalAttempts) {
        log.info("{} Healing exitoso | Score: {:.2f} | Intento: {}/{}",
            SUCCESS, score, attempt, totalAttempts);
    }

    /**
     * Registra un healing que no alcanzó el threshold.
     */
    public static void logHealingBelowThreshold(double score, double minScore) {
        log.warn("{} Healing por debajo del threshold | Score: {:.2f} (Min: {:.2f})",
            WARN, score, minScore);
    }

    /**
     * Registra un healing fallido.
     */
    public static void logHealingFailed(int failedCount) {
        log.warn("{} No se pudo recuperar elemento | Intentos fallidos: {}", FAIL, failedCount);
    }

    /**
     * Registra configuración válida.
     */
    public static void logConfigValid() {
        log.info("{} Configuración válida", SUCCESS);
    }

    /**
     * Registra error de configuración.
     */
    public static void logConfigError(String message) {
        log.error("{} {}", ERROR, message);
    }

    /**
     * Registra servidor alcanzable.
     */
    public static void logServerReachable(int attempt, int maxAttempts) {
        log.info("{} Servidor Healenium alcanzable en el intento {}/{}", SUCCESS, attempt, maxAttempts);
    }

    /**
     * Registra timeout al conectar.
     */
    public static void logServerTimeout(int attempt, int maxAttempts, String message) {
        log.warn("{} Timeout en intento {}/{}: {}", WARN, attempt, maxAttempts, message);
    }

    /**
     * Registra error de conexión.
     */
    public static void logConnectionError(int attempt, int maxAttempts, String message) {
        log.warn("{} Error de conexión en intento {}/{}: {}", WARN, attempt, maxAttempts, message);
    }

    /**
     * Registra fin de health check exitoso.
     */
    public static void logHealthCheckSuccess() {
        log.info("{} Health Check EXITOSO - Healenium está listo para usar", SUCCESS);
    }

    /**
     * Registra fin de health check fallido.
     */
    public static void logHealthCheckFailed() {
        log.error("{} Health Check FALLÓ", ERROR);
    }

    /**
     * Registra quality gate exitoso.
     */
    public static void logQualityGatePass(String gateName, String actual, String expected) {
        log.info("{} PASS Quality Gate: {} | Actual: {} | Esperado: {}",
            SUCCESS, gateName, actual, expected);
    }

    /**
     * Registra quality gate fallido.
     */
    public static void logQualityGateFail(String gateName, String actual, String expected) {
        log.warn("{} FAIL Quality Gate: {} | Actual: {} | Esperado: {}",
            FAIL, gateName, actual, expected);
    }

    /**
     * Registra reinicio de métricas.
     */
    public static void logMetricsReset() {
        log.debug("Reiniciando métricas de healing...");
    }
}

