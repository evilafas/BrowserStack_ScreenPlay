package stepDefinitions;

import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverHooks {

    @Before(order = 0)
    public void configurarWebDriver() {
        WebDriverManager.chromedriver().setup();
    }
}
