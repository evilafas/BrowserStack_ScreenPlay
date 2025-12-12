package com.browserstack.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class HomePageUI {

    public static final Target BTN_SIGNIN = Target.the("Boton de signin").
            located(By.xpath("//span[text()='Sign In']"));

    public static final Target TXT_USERNAME = Target.the("Texto de username").
            located(By.className("username"));

    public static final Target BTN_ADD_TO_CART = Target.the("Boton de anadir al carrito").
            located(By.xpath("(//div[@class='shelf-item__buy-btn'])[1]"));

    public static final Target BTN_CHECKOUT = Target.the("Boton de checkout").
            located(By.className("buy-btn"));

}
