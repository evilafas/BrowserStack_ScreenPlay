package com.browserstack.Interacctions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;

import static com.browserstack.userInterfaces.HomePageUI.BTN_ADD_TO_CART;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class AddProductToCart implements Interaction {


    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(BTN_ADD_TO_CART)
        );
    }

    public static AddProductToCart add(){
        return instrumented(AddProductToCart.class);
    }
}
