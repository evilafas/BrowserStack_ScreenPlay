package com.browserstack.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginPageUI {

    public static final Target INPUT_USERNAME = Target.the("Input username").
            located(By.xpath("(//input)[1]"));

    public static final Target INPUT_PASSWORD = Target.the("input password").
            located(By.xpath("(//input)[2]"));

    public static final Target BTN_LOGIN = Target.the("Boton de login").
            located(By.id("login-btn"));


}
