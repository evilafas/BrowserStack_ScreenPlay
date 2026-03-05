package com.browserstack.tasks;

import com.browserstack.models.UserData;
import com.browserstack.utils.ExcelReader;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Hit;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.Keys;

import static com.browserstack.userInterfaces.LoginPageUI.*;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class LoginTask implements Task {

    private static final String RUTA_DATOS = "src/test/resources/data/Credentials.xlsx";

    @Override
    public <T extends Actor> void performAs(T actor) {
        UserData userData = ExcelReader.readData(RUTA_DATOS);
        actor.attemptsTo(
                WaitUntil.the(INPUT_USERNAME, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue(userData.getUserName()).into(INPUT_USERNAME),
                Hit.the(Keys.ENTER).into(INPUT_USERNAME),
                Enter.theValue(userData.getPassword()).into(INPUT_PASSWORD),
                Hit.the(Keys.ENTER).into(INPUT_PASSWORD),
                Click.on(BTN_LOGIN)
        );
    }

    public static LoginTask login() {
        return instrumented(LoginTask.class);
    }
}
