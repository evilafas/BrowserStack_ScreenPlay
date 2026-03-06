package com.browserstack.listeners;

import com.browserstack.config.HealingQualityGate;
import com.browserstack.config.HealeniumLogger;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.openqa.selenium.WebElement;

/**
 * Test Listener para capturar eventos de Healenium.
 *
 * Extiende RunListener de JUnit 4 para interceptar eventos de ejecucion
 * y registrar metricas de healing para analisis posterior.
 */
@Slf4j
public class HealeniumTestListener extends RunListener {

    private final HealingQualityGate qualityGate = new HealingQualityGate();

    @Override
    public void testStarted(Description description) {
        HealeniumLogger.logScenarioStart(description.getMethodName());
        log.info("Iniciando test: {}", description.getMethodName());
    }

    @Override
    public void testFinished(Description description) {
        qualityGate.generateHealingReport();
        log.info("Test finalizado: {}", description.getMethodName());
    }

    @Override
    public void testFailure(Failure failure) {
        HealeniumLogger.logScenarioFailed(failure.getDescription().getMethodName());
        qualityGate.generateHealingReport();

        Throwable throwable = failure.getException();
        if (throwable != null && isElementNotFoundException(throwable)) {
            log.warn("Test fallo por elemento no encontrado. Posiblemente el selector cambio en la pagina.");
            log.warn("Recomendacion: Actualiza los selectores en las clases UI correspondientes.");
        }

        log.error("Test fallo: {}", failure.getDescription().getMethodName(), throwable);
    }

    @Override
    public void testIgnored(Description description) {
        log.warn("Test omitido: {}", description.getMethodName());
    }

    /**
     * Metodo llamado cuando Healenium repara un elemento.
     */
    public void onHealingAttempt(WebElement healedElement, String originalLocator, String healedLocator, double score) {
        if (qualityGate.recordSuccessfulHeal(score)) {
            HealeniumLogger.logHealingSuccess(score, 1, 1);
            log.info("Elemento sanado | Original: {} | Nuevo: {} | Score: {}", originalLocator, healedLocator, score);
        } else {
            HealeniumLogger.logHealingBelowThreshold(score, qualityGate.getAverageScore());
            log.warn("Healing por debajo del threshold | Score: {}", score);
        }
    }

    /**
     * Metodo llamado cuando Healenium no puede reparar un elemento.
     */
    public void onHealingFailed(String locator, int attempts) {
        qualityGate.recordFailedHeal();
        HealeniumLogger.logHealingFailed(attempts);
        log.error("No se pudo sanar elemento despues de {} intentos | Locator: {}", attempts, locator);
    }

    private boolean isElementNotFoundException(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) return false;

        return message.contains("Unable to locate element") ||
               message.contains("no such element") ||
               message.contains("ElementNotFoundException") ||
               message.contains("NoSuchElementException");
    }
}
