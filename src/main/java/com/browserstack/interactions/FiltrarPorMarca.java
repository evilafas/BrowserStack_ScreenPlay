package com.browserstack.interactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class FiltrarPorMarca implements Interaction {

    private String marca;

    public FiltrarPorMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Target OPC_MARCA = Target.the("Opcion de marca " + marca)
                .located(By.xpath("//*[text()='" + marca + "']"));
        actor.attemptsTo(
                Click.on(OPC_MARCA)
        );
    }

    public static FiltrarPorMarca filter(String marca) {
        return instrumented(FiltrarPorMarca.class, marca);
    }
}
