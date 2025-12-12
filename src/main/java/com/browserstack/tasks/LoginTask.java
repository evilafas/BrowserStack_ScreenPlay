package com.browserstack.tasks;

import com.browserstack.models.UserData;
import com.browserstack.utils.LeerExcel;
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

    String dataExcel = "src/test/resources/data/Credentials.xlsx";
    UserData userData = LeerExcel.readData(dataExcel);

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(INPUT_USERNAME, isVisible()).forNoMoreThan(10).seconds(),
                Enter.theValue("demouser").into(INPUT_USERNAME),
                Hit.the(Keys.ENTER).into(INPUT_USERNAME),
                Enter.theValue("testingisfun99").into(INPUT_PASSWORD),
                Hit.the(Keys.ENTER).into(INPUT_PASSWORD),
                Click.on(BTN_LOGIN)
        );
    }

    public static LoginTask login(){
        return instrumented(LoginTask.class);
    }
}
