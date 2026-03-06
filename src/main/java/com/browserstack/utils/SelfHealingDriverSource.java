package com.browserstack.utils;

import com.epam.healenium.SelfHealingDriver;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Proveedor de WebDriver con capacidad de Self-Healing automático usando Healenium.
 *
 * Requisitos:
 * - Servidor Healenium corriendo en localhost:7878 (docker-compose up -d)
 * - hlm-selector-imitator corriendo en localhost:8000
 */
@Slf4j
public class SelfHealingDriverSource implements DriverSource {

    private static final String HLM_SERVER_URL  = "http://localhost:7878";
    private static final String HLM_IMITATOR_URL = "http://localhost:8000";

    @Override
    public WebDriver newDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        ChromeDriver delegate = new ChromeDriver(options);
        log.info("ChromeDriver creado. Envolviendo con SelfHealingDriver...");

        try {
            Config config = ConfigFactory.parseString(
                "hlm.server.url = \"" + HLM_SERVER_URL + "\"\n" +
                "hlm.imitator.url = \"" + HLM_IMITATOR_URL + "\"\n" +
                "recovery-tries = 1\n" +
                "score-cap = .6\n" +
                "heal-enabled = true\n" +
                "backlight-healing = true\n" +
                "proxy = false\n"
            );

            WebDriver selfHealingDriver = SelfHealingDriver.create(delegate, config);
            log.info("SelfHealingDriver inicializado correctamente. Self-Healing HABILITADO.");
            return selfHealingDriver;

        } catch (Exception e) {
            log.error("No se pudo crear SelfHealingDriver — usando ChromeDriver sin healing.", e);
            log.warn("Verifica que docker-compose está levantado: docker-compose up -d");
            return delegate;
        }
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }
}
