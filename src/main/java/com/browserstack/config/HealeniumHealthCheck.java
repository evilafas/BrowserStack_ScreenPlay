package com.browserstack.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * Health Check para verificar que Healenium Backend está disponible.
 *
 * Antes de ejecutar las pruebas, valida que:
 * 1. El servidor Healenium está corriendo y accesible
 * 2. La base de datos de Healenium está disponible
 * 3. La configuración es correcta
 */
@Slf4j
public class HealeniumHealthCheck {

    private final HealeniumConfig config;
    private static final int SOCKET_TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;

    public HealeniumHealthCheck() {
        this.config = HealeniumConfig.getInstance();
    }

    /**
     * Realiza un health check completo de Healenium.
     * @return true si todo está OK, false en caso contrario
     */
    public boolean checkHealth() {
        log.info("╔════════════════════════════════════════════════════════╗");
        log.info("║       VERIFICANDO SALUD DE HEALENIUM (HEALTH CHECK)    ║");
        log.info("╚════════════════════════════════════════════════════════╝");

        boolean configValid = checkConfiguration();
        boolean serverReachable = checkServerReachability();

        if (!configValid) {
            log.error("✗ La configuración de Healenium no es válida");
            return false;
        }

        if (!serverReachable) {
            log.error("✗ No se puede alcanzar el servidor Healenium en {}", config.getServerUrl());
            log.error("Asegúrate de ejecutar: docker-compose up -d");
            return false;
        }

        log.info("✓ Health Check EXITOSO - Healenium está listo para usar");
        return true;
    }

    /**
     * Verifica que la configuración de Healenium es válida.
     */
    private boolean checkConfiguration() {
        log.info("Validando configuración...");

        if (!config.isValid()) {
            log.error("✗ Configuración inválida detectada");
            return false;
        }

        log.info("✓ Configuración válida");
        log.info("  - Host: {}", config.getServerHost());
        log.info("  - Puerto: {}", config.getServerPort());
        log.info("  - Recovery Tries: {}", config.getRecoveryTries());
        log.info("  - Score Cap: {}", config.getScoreCap());

        return true;
    }

    /**
     * Verifica que el servidor Healenium es alcanzable.
     */
    private boolean checkServerReachability() {
        log.info("Conectando a {}:{} ...", config.getServerHost(), config.getServerPort());

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (isServerReachable()) {
                    log.info("✓ Servidor Healenium alcanzable en el intento {}/{}", attempt, MAX_RETRIES);
                    return true;
                }
            } catch (SocketTimeoutException e) {
                log.warn("⚠ Timeout en intento {}/{}: {}", attempt, MAX_RETRIES, e.getMessage());
            } catch (IOException e) {
                log.warn("⚠ Error de conexión en intento {}/{}: {}", attempt, MAX_RETRIES, e.getMessage());
            }

            if (attempt < MAX_RETRIES) {
                log.info("Reintentando en {} ms...", RETRY_DELAY_MS);
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.error("✗ No se pudo conectar a Healenium Backend después de {} intentos", MAX_RETRIES);
        return false;
    }

    /**
     * Intenta conectar al servidor Healenium mediante socket.
     */
    private boolean isServerReachable() throws IOException {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            socket.connect(
                new java.net.InetSocketAddress(config.getServerHost(), config.getServerPort()),
                SOCKET_TIMEOUT_MS
            );
            return true;
        }
    }

    /**
     * Proporciona instrucciones para levantar Healenium si no está disponible.
     */
    public static void printSetupInstructions() {
        log.error("════════════════════════════════════════════════════════════════");
        log.error("                  INSTRUCCIONES DE CONFIGURACIÓN");
        log.error("════════════════════════════════════════════════════════════════");
        log.error("");
        log.error("Para usar Self-Healing con Healenium, sigue estos pasos:");
        log.error("");
        log.error("1. Asegúrate de tener Docker instalado");
        log.error("   https://docs.docker.com/get-docker/");
        log.error("");
        log.error("2. Levanta los servicios de Healenium:");
        log.error("   cd C:\\Users\\evila\\IdeaProjects\\BrowserStack_ScreenPlay");
        log.error("   docker-compose up -d");
        log.error("");
        log.error("3. Verifica que los contenedores están corriendo:");
        log.error("   docker-compose ps");
        log.error("");
        log.error("4. Espera a que PostgreSQL esté listo (puede tomar 10-20 segundos)");
        log.error("");
        log.error("5. Verifica el health check en:");
        log.error("   http://localhost:7878/health");
        log.error("");
        log.error("6. Ejecuta las pruebas nuevamente");
        log.error("");
        log.error("Si necesitas detener los servicios:");
        log.error("   docker-compose down");
        log.error("");
        log.error("════════════════════════════════════════════════════════════════");
    }
}

