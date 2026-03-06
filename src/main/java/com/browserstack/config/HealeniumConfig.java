package com.browserstack.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración centralizada de Healenium.
 *
 * Lee las propiedades desde healenium.properties y las expone para uso en toda la aplicación.
 * Esta clase permite acceso programático a la configuración de Healenium sin necesidad
 * de cargar el archivo properties manualmente en cada lugar.
 */
@Slf4j
@Getter
public class HealeniumConfig {

    private static final String CONFIG_FILE = "healenium.properties";
    private static HealeniumConfig instance;

    private final int recoveryTries;
    private final double scoreCap;
    private final boolean healEnabled;
    private final String serverHost;
    private final int serverPort;

    private HealeniumConfig() {
        Properties props = loadProperties();
        this.recoveryTries = Integer.parseInt(
            props.getProperty("recovery-tries", "1")
        );
        this.scoreCap = Double.parseDouble(
            props.getProperty("score-cap", "0.6")
        );
        this.healEnabled = Boolean.parseBoolean(
            props.getProperty("heal-enabled", "true")
        );
        this.serverHost = props.getProperty("hlm.server.host", "localhost");
        this.serverPort = Integer.parseInt(
            props.getProperty("hlm.server.port", "7878")
        );

        logConfiguration();
    }

    /**
     * Obtiene la instancia singleton de HealeniumConfig.
     */
    public static synchronized HealeniumConfig getInstance() {
        if (instance == null) {
            instance = new HealeniumConfig();
        }
        return instance;
    }

    /**
     * Carga las propiedades desde el archivo healenium.properties.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                log.warn("Archivo {} no encontrado. Usando valores por defecto.", CONFIG_FILE);
                return props;
            }
            props.load(input);
            log.debug("Propiedades de Healenium cargadas desde: {}", CONFIG_FILE);
        } catch (IOException e) {
            log.error("Error al cargar {}: {}", CONFIG_FILE, e.getMessage(), e);
        }
        return props;
    }

    /**
     * Registra la configuración actual en los logs.
     */
    private void logConfiguration() {
        log.info("=== CONFIGURACIÓN HEALENIUM ===");
        log.info("Self-Healing Habilitado: {}", healEnabled);
        log.info("Intentos de Recuperación: {}", recoveryTries);
        log.info("Score Mínimo de Similitud: {}", scoreCap);
        log.info("Servidor Healenium: {}:{}", serverHost, serverPort);
        log.info("================================");
    }

    /**
     * Obtiene la URL del servidor Healenium.
     */
    public String getServerUrl() {
        return "http://" + serverHost + ":" + serverPort;
    }

    /**
     * Valida la configuración de Healenium.
     * @return true si la configuración es válida, false en caso contrario.
     */
    public boolean isValid() {
        boolean valid = true;

        if (recoveryTries < 0) {
            log.error("recovery-tries debe ser >= 0, actual: {}", recoveryTries);
            valid = false;
        }

        if (scoreCap < 0 || scoreCap > 1) {
            log.error("score-cap debe estar entre 0.0 y 1.0, actual: {}", scoreCap);
            valid = false;
        }

        if (serverPort < 1 || serverPort > 65535) {
            log.error("hlm.server.port debe estar entre 1 y 65535, actual: {}", serverPort);
            valid = false;
        }

        if (valid) {
            log.info("Configuración de Healenium válida ✓");
        }

        return valid;
    }
}

