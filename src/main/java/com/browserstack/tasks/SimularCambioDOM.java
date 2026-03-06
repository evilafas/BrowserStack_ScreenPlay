package com.browserstack.tasks;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Task de DEMO para verificar self-healing de Healenium.
 *
 * Simula un cambio en el DOM de la página (como si el front-end hubiera
 * renombrado el atributo id del botón de login) usando JavaScript.
 *
 * Flujo de verificación:
 * 1. Ejecutar el test de login UNA VEZ con BTN_LOGIN = By.id("login-btn")  → Healenium guarda snapshot
 * 2. Agregar esta Task ANTES de Click.on(BTN_LOGIN) en LoginTask
 * 3. Ejecutar el test de nuevo → Healenium detecta que "login-btn" no existe,
 *    busca el snapshot, encuentra el botón renombrado → HEALING activado
 */
@Slf4j
public class SimularCambioDOM implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Simula que el equipo front-end renombró el id del botón
        js.executeScript(
            "var btn = document.getElementById('login-btn');" +
            "if (btn) { btn.id = 'login-btn-v2'; console.log('DOM modificado: login-btn → login-btn-v2'); }" +
            "else { console.warn('Botón login-btn no encontrado'); }"
        );
        log.info("DOM modificado via JS: 'login-btn' → 'login-btn-v2' (simulando cambio de front-end)");
    }

    public static SimularCambioDOM ahora() {
        return instrumented(SimularCambioDOM.class);
    }
}
