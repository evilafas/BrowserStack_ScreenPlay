package com.browserstack.config;

import lombok.extern.slf4j.Slf4j;

/**
 * Quality Gates para validar el éxito y comportamiento del Self-Healing.
 *
 * Implementa una serie de validaciones para asegurar que Healenium está funcionando
 * correctamente y que los elementos se están recuperando con una calidad aceptable.
 */
@Slf4j
public class HealingQualityGate {

    private final HealeniumConfig config;
    private int totalHealingAttempts = 0;
    private int successfulHeals = 0;
    private double totalScoreSum = 0;
    private int unrecoverableElements = 0;

    public HealingQualityGate() {
        this.config = HealeniumConfig.getInstance();
    }

    /**
     * Registra un intento de healing exitoso.
     * @param score Puntuación de similitud obtenida (0.0 - 1.0)
     * @return true si el score cumple con los requisitos mínimos, false en caso contrario
     */
    public boolean recordSuccessfulHeal(double score) {
        totalHealingAttempts++;
        successfulHeals++;
        totalScoreSum += score;

        boolean meetsThreshold = score >= config.getScoreCap();

        if (meetsThreshold) {
            log.info("✓ Healing exitoso | Score: {:.2f} | Intento: {}/{}",
                score, totalHealingAttempts, successfulHeals);
        } else {
            log.warn("⚠ Healing por debajo del threshold | Score: {:.2f} (Min: {:.2f})",
                score, config.getScoreCap());
        }

        return meetsThreshold;
    }

    /**
     * Registra un intento de healing fallido.
     */
    public void recordFailedHeal() {
        totalHealingAttempts++;
        unrecoverableElements++;
        log.warn("✗ No se pudo recuperar elemento | Intentos fallidos: {}", unrecoverableElements);
    }

    /**
     * Quality Gate 1: Valida que la tasa de éxito de healing sea aceptable.
     * @param minimumSuccessRate Porcentaje mínimo de éxito esperado (0.0 - 1.0)
     * @return true si cumple, false en caso contrario
     */
    public boolean validateSuccessRate(double minimumSuccessRate) {
        if (totalHealingAttempts == 0) {
            log.info("No hubo intentos de healing");
            return true;
        }

        double successRate = (double) successfulHeals / totalHealingAttempts;
        boolean passes = successRate >= minimumSuccessRate;

        String status = passes ? "✓ PASS" : "✗ FAIL";
        log.info("{} Quality Gate: Success Rate | Actual: {:.2f}% | Esperado: {:.2f}%",
            status, successRate * 100, minimumSuccessRate * 100);

        return passes;
    }

    /**
     * Quality Gate 2: Valida que el score promedio sea aceptable.
     * @return true si cumple, false en caso contrario
     */
    public boolean validateAverageScore() {
        if (successfulHeals == 0) {
            log.info("No hubo heals exitosos para validar score promedio");
            return true;
        }

        double averageScore = totalScoreSum / successfulHeals;
        boolean passes = averageScore >= config.getScoreCap();

        String status = passes ? "✓ PASS" : "✗ FAIL";
        log.info("{} Quality Gate: Average Score | Actual: {:.2f} | Mínimo: {:.2f}",
            status, averageScore, config.getScoreCap());

        return passes;
    }

    /**
     * Quality Gate 3: Valida que no se exceda el límite de intentos de recuperación.
     * @return true si cumple, false en caso contrario
     */
    public boolean validateRecoveryTries() {
        boolean passes = unrecoverableElements <= config.getRecoveryTries();

        String status = passes ? "✓ PASS" : "✗ FAIL";
        log.info("{} Quality Gate: Recovery Tries | Elementos no recuperables: {} | Límite: {}",
            status, unrecoverableElements, config.getRecoveryTries());

        return passes;
    }

    /**
     * Genera un reporte completo de healing.
     */
    public void generateHealingReport() {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║          REPORTE DE SELF-HEALING (HEALING REPORT)      ║");
        log.info("╠════════════════════════════════════════════════════════╣");
        log.info("║ Total de intentos de healing:        {}", String.format("%-18d", totalHealingAttempts));
        log.info("║ Heals exitosos:                     {}", String.format("%-18d", successfulHeals));
        log.info("║ Elementos no recuperables:          {}", String.format("%-18d", unrecoverableElements));

        if (successfulHeals > 0) {
            double avgScore = totalScoreSum / successfulHeals;
            log.info("║ Score promedio de similitud:        {}", String.format("%-18.2f", avgScore));
        }

        if (totalHealingAttempts > 0) {
            double successRate = (double) successfulHeals / totalHealingAttempts;
            log.info("║ Tasa de éxito:                      {}", String.format("%-17.1f%%", successRate * 100));
        }

        log.info("║ Configuración Healenium:                              ║");
        log.info("║  - Recovery tries:                  {}", String.format("%-18d", config.getRecoveryTries()));
        log.info("║  - Score cap (mínimo):              {}", String.format("%-18.2f", config.getScoreCap()));
        log.info("║  - Self-healing habilitado:         {}", String.format("%-18s", config.isHealEnabled() ? "Sí" : "No"));
        log.info("║  - Servidor:                        {}", String.format("%-18s", config.getServerUrl()));
        log.info("╚════════════════════════════════════════════════════════╝");
    }

    /**
     * Reinicia las métricas de healing.
     */
    public void reset() {
        log.debug("Reiniciando métricas de healing...");
        totalHealingAttempts = 0;
        successfulHeals = 0;
        totalScoreSum = 0;
        unrecoverableElements = 0;
    }

    // Getters para acceder a las métricas
    public int getTotalHealingAttempts() {
        return totalHealingAttempts;
    }

    public int getSuccessfulHeals() {
        return successfulHeals;
    }

    public double getAverageScore() {
        return successfulHeals > 0 ? totalScoreSum / successfulHeals : 0;
    }

    public double getSuccessRate() {
        return totalHealingAttempts > 0 ? (double) successfulHeals / totalHealingAttempts : 0;
    }

    public int getUnrecoverableElements() {
        return unrecoverableElements;
    }
}

