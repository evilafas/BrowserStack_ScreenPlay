package com.browserstack.tasks;

import com.browserstack.models.UserData;
import com.browserstack.utils.ExcelReader;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static com.browserstack.userInterfaces.CheckoutPageUI.*;
import static com.browserstack.userInterfaces.HomePageUI.BTN_CHECKOUT;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class CheckoutTask implements Task {

    private static final String RUTA_DATOS = "src/test/resources/data/Credentials.xlsx";

    @Override
    public <T extends Actor> void performAs(T actor) {
        UserData userData = ExcelReader.readData(RUTA_DATOS);
        actor.attemptsTo(
                Click.on(BTN_CHECKOUT),
                WaitUntil.the(TXT_FIRST_NAME, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue(userData.getFirstName()).into(TXT_FIRST_NAME),
                Enter.theValue(userData.getLastName()).into(TXT_LAST_NAME),
                Enter.theValue(userData.getAddress()).into(TXT_ADDRESS),
                Enter.theValue(userData.getState()).into(TXT_STATE),
                Enter.theValue(userData.getPostalCode()).into(TXT_POSTAL_CODE),
                WaitUntil.the(BTN_SUBMIT, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(BTN_SUBMIT)
        );
    }

    public static CheckoutTask checkout() {
        return instrumented(CheckoutTask.class);
    }
}
