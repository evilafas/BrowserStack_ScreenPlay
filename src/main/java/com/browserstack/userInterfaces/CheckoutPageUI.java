package com.browserstack.userInterfaces;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class CheckoutPageUI {

    public static final Target TXT_FIRST_NAME =
            Target.the("Campo nombre")
                    .located(By.id("firstNameInput"));

    public static final Target TXT_LAST_NAME =
            Target.the("Campo apellido")
                    .located(By.id("lastNameInput"));

    public static final Target TXT_ADDRESS =
            Target.the("Campo direccion")
                    .located(By.id("addressLine1Input"));

    public static final Target TXT_STATE =
            Target.the("Campo estado/provincia")
                    .located(By.id("provinceInput"));

    public static final Target TXT_POSTAL_CODE =
            Target.the("Campo codigo postal")
                    .located(By.id("postCodeInput"));

    public static final Target BTN_SUBMIT =
            Target.the("Boton continuar/enviar")
                    .located(By.id("checkout-shipping-continue"));

    public static final Target LBL_ORDER_CONFIRMATION =
            Target.the("Mensaje de confirmacion de orden")
                    .located(By.xpath("//*[contains(text(),'successfully placed')]"));
}
