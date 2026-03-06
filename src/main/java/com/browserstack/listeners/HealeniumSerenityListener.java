package com.browserstack.listeners;

import com.browserstack.config.HealingQualityGate;
import com.browserstack.config.HealeniumLogger;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.core.listeners.AbstractStepListener;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.steps.StepFailure;
import org.openqa.selenium.WebElement;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Step Listener para Serenity BDD que captura eventos de Healenium.
 *
 * Registrado via META-INF/services/net.thucydides.core.steps.StepListener (ServiceLoader).
 */
@Slf4j
public class HealeniumSerenityListener extends AbstractStepListener {

    private final HealingQualityGate qualityGate = new HealingQualityGate();
    private String currentScenarioName = "Unknown";

    // --- Métodos abstractos no implementados en AbstractStepListener ---

    @Override
    public void testStarted(String description, String id) {
        testStarted(description);
    }

    @Override
    public void testStarted(String description, String id, ZonedDateTime startTime) {
        testStarted(description);
    }

    @Override
    public void testFinished(TestOutcome outcome, boolean inProgress, ZonedDateTime timestamp) {
        testFinished(outcome);
    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshots) {
    }

    @Override
    public void testRunFinished() {
        qualityGate.generateHealingReport();
    }

    @Override
    public void takeScreenshots(List<ScreenshotAndHtmlSource> screenshots) {
    }

    // --- Lógica de Healenium ---

    @Override
    public void testSuiteFinished() {
        qualityGate.generateHealingReport();
    }

    @Override
    public void testStarted(String description) {
        currentScenarioName = description;
        HealeniumLogger.logScenarioStart(description);
    }

    @Override
    public void testFinished(TestOutcome outcome) {
        if (outcome.isSuccess()) {
            HealeniumLogger.logScenarioSuccess(currentScenarioName);
        } else {
            HealeniumLogger.logScenarioFailed(currentScenarioName);
            analyzeFailure(outcome);
        }
        qualityGate.generateHealingReport();
    }

    @Override
    public void stepFailed(StepFailure failure) {
        log.warn("Step fallo: {}", failure.getDescription());
    }

    public void onHealingAttempt(WebElement healedElement, String originalLocator, String healedLocator, double score) {
        if (qualityGate.recordSuccessfulHeal(score)) {
            HealeniumLogger.logHealingSuccess(score, 1, 1);
        } else {
            HealeniumLogger.logHealingBelowThreshold(score, qualityGate.getAverageScore());
        }
    }

    public void onHealingFailed(String locator, int attempts) {
        qualityGate.recordFailedHeal();
        HealeniumLogger.logHealingFailed(attempts);
    }

    private void analyzeFailure(TestOutcome outcome) {
        if (outcome.getTestFailureCause() != null) {
            Throwable cause = outcome.getTestFailureCause().getOriginalCause();
            if (cause != null && isElementNotFoundException(cause)) {
                log.warn("Test fallo por elemento no encontrado. Posiblemente el selector cambio en la pagina.");
            }
        }
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
