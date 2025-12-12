package com.browserstack.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class CheckoutPageUI {

    public static final Target INPUT_1 =
            Target.the("input")
                    .located(By.xpath("//*[@id='firstNameInput']"));

    public static final Target lastname =
            Target.the("Last")
                    .located(By.xpath("//*[@id='lastNameInput']"));

    public static final Target direccion =
            Target.the("Address field")
                    .located(By.name("addressLine1Input"));

    public static final Target STATE =
            Target.the("state")
                    .located(By.xpath("//input"));

    public static final Target POSTAL =
            Target.the("postal")
                    .located(By.xpath("(//input)[5]"));

    public static final Target CONFIRM =
            Target.the("confirmation text")
                    .located(By.xpath("//*[contains(text(),'confirm')]"));

    public static final Target BTN_SUBMIT =
            Target.the("submit button")
                    .located(By.xpath("//*[@id='checkout-shipping-continue']"));

    public static final Target BTN_NEXT =
            Target.the("next")
                    .located(By.xpath("//*[@id='checkout-shipping-continue']"));


    public static final Target RANDOM =
            Target.the("random element")
                    .located(By.xpath("//*"));

}
