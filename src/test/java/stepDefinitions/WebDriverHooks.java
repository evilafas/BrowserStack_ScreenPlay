package stepDefinitions;

import com.browserstack.config.HealeniumHealthCheck;
import com.browserstack.config.HealingQualityGate;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.actors.OnlineCast;

import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;

/**
 * Hooks de Cucumber para configurar el escenario y validar Healenium.
 *
 * Se ejecutan antes y después de cada escenario para:
 * - Validar que Healenium está disponible
 * - Configurar el ambiente de prueba
 * - Registrar métricas de healing
 */
@Slf4j
public class WebDriverHooks {

    private static final HealeniumHealthCheck healthCheck = new HealeniumHealthCheck();
    private static final HealingQualityGate qualityGate = new HealingQualityGate();
    private static boolean healthCheckPassed = false;

    @Before(order = 0)
    public void validarHealenium() {
        if (!healthCheckPassed) {
            log.info("═══════════════════════════════════════════════════════");
            log.info("PRIMER ESCENARIO - Realizando Health Check de Healenium");
            log.info("═══════════════════════════════════════════════════════");

            if (healthCheck.checkHealth()) {
                healthCheckPassed = true;
                log.info("Healenium Health Check EXITOSO - Self-Healing activo");
            } else {
                healthCheckPassed = true; // no bloquear tests subsiguientes
                HealeniumHealthCheck.printSetupInstructions();
                log.warn("Healenium no esta disponible. Los tests continuaran SIN self-healing.");
                log.warn("Para activar self-healing ejecuta: docker-compose up -d");
            }
        }
    }

    @Before(order = 1)
    public void configurarEscenario(Scenario scenario) {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║ Iniciando escenario: {}", scenario.getName());
        log.info("╚════════════════════════════════════════════════════════╝");
        setTheStage(new OnlineCast());
    }

    @After(order = 0)
    public void generarReporteHealing(Scenario scenario) {
        String status = scenario.isFailed() ? "❌ FALLÓ" : "✓ PASÓ";
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║ Escenario completado: {} {}", scenario.getName(), status);
        log.info("╚════════════════════════════════════════════════════════╝");

        // Genera el reporte de healing
        qualityGate.generateHealingReport();

        // Reinicia las métricas para el siguiente escenario
        qualityGate.reset();
    }

    /**
     * Obtiene la instancia singleton del Quality Gate.
     */
    public static HealingQualityGate getQualityGate() {
        return qualityGate;
    }
}
