package com.browserstack.base;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.runner.RunWith;

/**
 * Clase base para tests JUnit directos con Serenity + Healenium.
 *
 * Nota: Los runners Cucumber usan @RunWith(CucumberWithSerenity.class)
 * y no extienden esta clase. Esta base es para tests JUnit unitarios/directos.
 * La integracion de Healenium con Cucumber se gestiona via:
 *  - SelfHealingDriverSource en serenity.properties (webdriver.provided.classname)
 *  - HealeniumSerenityListener via META-INF/services
 *  - WebDriverHooks (@Before/@After en stepDefinitions)
 */
@RunWith(SerenityRunner.class)
public abstract class HealeniumBaseTest {
}
