package com.browserstack.Interacctions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;
import static com.browserstack.userInterfaces.HomePageUI.BTN_SIGNIN;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;

public class OpenWebTask implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url("https://bstackdemo.com/"),
                WaitUntil.the(BTN_SIGNIN, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(BTN_SIGNIN)
        );
    }

    public static OpenWebTask openWeb(){
        return instrumented(OpenWebTask.class);
    }
}
